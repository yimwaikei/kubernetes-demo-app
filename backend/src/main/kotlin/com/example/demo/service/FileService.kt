package com.example.demo.service

import com.example.demo.config.MinioConfig
import io.minio.BucketExistsArgs
import io.minio.GetPresignedObjectUrlArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.http.Method
import jakarta.annotation.PostConstruct
import org.apache.coyote.BadRequestException
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.Instant

@Service
class FileService(
    private val minioClient: MinioClient,
    private val minioConfig: MinioConfig
) {

    @PostConstruct
    fun initBucket() {
        val bucketName = minioConfig.defaultBucket

        val exists = minioClient.bucketExists(
            BucketExistsArgs
                .builder()
                .bucket(bucketName)
                .build()
        )

        if (!exists) {
            minioClient.makeBucket(
                MakeBucketArgs
                    .builder()
                    .bucket(bucketName)
                    .build()
            )
        }
    }

    fun uploadFile(file: MultipartFile): String {
        if (!(isFileImage(file))) throw IllegalArgumentException("File image is invalid")
        return uploadToMinio(file)
    }

    fun generatePresignedUrl(filePath: String, expiresSeconds: Int = 300): String {
        if (filePath == "") throw NoSuchElementException("File does not exist")
        val parts = filePath.split("/", limit = 2)
        if (parts.size != 2) throw IllegalArgumentException("Invalid MinIO path: $filePath")

        val bucket = parts[0]
        val objectName = parts[1]

        val args = GetPresignedObjectUrlArgs.builder()
            .method(Method.GET)
            .bucket(bucket)
            .`object`(objectName)
            .expiry(expiresSeconds)
            .build()

        return minioClient.getPresignedObjectUrl(args)
    }

    private fun isFileImage(file: MultipartFile): Boolean {
        val mimeType: String? = file.contentType

        return (mimeType != null && mimeType.startsWith("image/"))
    }

    private fun generateNewFilename(file: MultipartFile): String {
        val original = file.originalFilename ?: "file"
        val name = original.substringBeforeLast(".")
        val extension = original.substringAfterLast(".", "")
        val timestamp = Instant.now().toEpochMilli()

        return if (extension.isNotEmpty()) {
            "$name-$timestamp.$extension"
        } else {
            "$name-$timestamp"
        }
    }

    private fun uploadToMinio(file: MultipartFile): String {
        try {
            val args = PutObjectArgs.builder()
                .bucket(minioConfig.defaultBucket)
                .`object`(generateNewFilename(file))
                .contentType(file.contentType)
                .stream(file.inputStream, file.size, -1)
                .build()

            val response = minioClient.putObject(args)

            return "${response.bucket()}/${response.`object`()}"
        } catch (e: Exception) {
            throw BadRequestException("Failed to upload file", e)
        }
    }
}
