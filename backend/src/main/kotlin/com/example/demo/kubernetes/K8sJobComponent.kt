package com.example.demo.kubernetes

import io.fabric8.kubernetes.api.model.batch.v1.Job
import io.fabric8.kubernetes.api.model.batch.v1.JobBuilder
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.KubernetesClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties
import org.springframework.stereotype.Component
import java.net.URI
import java.util.UUID

@Component
class K8sJobComponent(
    private val dataSourceProperties: DataSourceProperties
) {

    @Value("\${k8s.job.transform-image.image}")
    lateinit var transformImageJobImage: String

    @Value("\${minio.endpoint}")
    lateinit var minioEndpoint: String

    @Value("\${minio.access-key}")
    lateinit var minioUser: String

    @Value("\${minio.secret-key}")
    lateinit var minioPassword: String

    @Value("\${k8s.namespace}")
    lateinit var namespace: String

    private val kubernetesClient: KubernetesClient = KubernetesClientBuilder().build()

    fun triggerTransformImageJob(jobId: String) {
        val jobName = "transform-image-job-$jobId-${UUID.randomUUID().toString().take(8)}"

        val db = parseDatabaseConfig()
        val minio = parseMinioConfig()

        val job = buildJob(namespace, jobName, db, minio, jobId)

        try {
            kubernetesClient.batch()
                .v1()
                .jobs()
                .inNamespace(namespace)
                .resource(job)
                .create()

            println("Kubernetes Job created: $jobName in namespace $namespace")

        } catch (ex: Exception) {
            println("Failed to create Kubernetes Job: $jobName")
            ex.printStackTrace()
            throw RuntimeException("Failed to trigger Kubernetes job '$jobId': ${ex.message}")
        }
    }

    private fun parseDatabaseConfig(): DbConfig {

        val dbUrl = dataSourceProperties.url
            ?: throw RuntimeException("Datasource URL not configured")

        val dbUser = dataSourceProperties.username
            ?: throw RuntimeException("Datasource username not configured")

        val dbPassword = dataSourceProperties.password
            ?: throw RuntimeException("Datasource password not configured")

        val uri = URI(dbUrl.removePrefix("jdbc:"))

        val host = uri.host
        val port = if (uri.port == -1) "5432" else uri.port.toString()
        val dbName = uri.path.removePrefix("/")

        return DbConfig(host, port, dbName, dbUser, dbPassword)
    }

    private fun parseMinioConfig(): MinioConfig {

        val uri = URI(minioEndpoint)

        val host = uri.host
        val port = if (uri.port == -1) "9000" else uri.port.toString()

        return MinioConfig(host, port, minioUser, minioPassword)
    }

    private fun buildJob(
        namespace: String,
        jobName: String,
        db: DbConfig,
        minio: MinioConfig,
        jobId: String,
    ): Job {

        return JobBuilder()
            .withNewMetadata()
            .withName(jobName)
            .withNamespace(namespace)
            .endMetadata()
            .withNewSpec()
            .withTtlSecondsAfterFinished(300)
            .withNewTemplate()
            .withNewSpec()
            .withRestartPolicy("Never")
            .addNewImagePullSecret("ghcr-secret")
            .addNewContainer()
            .withName("python-job")
            .withImage(transformImageJobImage)
            .withCommand("python", "transform_image.py")

            /* PostgreSQL */
            .addNewEnv().withName("DB_HOST").withValue(db.host).endEnv()
            .addNewEnv().withName("DB_PORT").withValue(db.port).endEnv()
            .addNewEnv().withName("DB_NAME").withValue(db.name).endEnv()
            .addNewEnv().withName("DB_USER").withValue(db.user).endEnv()
            .addNewEnv().withName("DB_PASSWORD").withValue(db.password).endEnv()

            /* MinIO */
            .addNewEnv().withName("MINIO_HOST").withValue(minio.host).endEnv()
            .addNewEnv().withName("MINIO_PORT").withValue(minio.port).endEnv()
            .addNewEnv().withName("MINIO_USER").withValue(minio.user).endEnv()
            .addNewEnv().withName("MINIO_PASSWORD").withValue(minio.password).endEnv()

            /* Record */
            .addNewEnv().withName("RECORD_ID").withValue(jobId).endEnv()

            .endContainer()
            .endSpec()
            .endTemplate()
            .endSpec()
            .build()
    }

    data class DbConfig(
        val host: String,
        val port: String,
        val name: String,
        val user: String,
        val password: String
    )

    data class MinioConfig(
        val host: String,
        val port: String,
        val user: String,
        val password: String
    )
}
