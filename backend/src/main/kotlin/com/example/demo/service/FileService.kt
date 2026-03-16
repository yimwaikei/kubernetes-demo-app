package com.example.demo.service

import com.example.demo.config.MinioConfig
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
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
