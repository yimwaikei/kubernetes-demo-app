package com.example.demo.controller

import io.minio.MinioClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/minio")
class MinioController(
    private val minioClient: MinioClient
) {
    @GetMapping("/buckets")
    fun listBuckets(): List<String> {
        return minioClient.listBuckets().map { it.name() }
    }
}