package com.example.demo.controller

import com.example.demo.dto.FileDto
import com.example.demo.service.FileService
import com.example.demo.service.JobService
import io.minio.MinioClient
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private val fileService: FileService
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<FileDto> {
        val filePath = fileService.uploadFile(file)
        return ResponseEntity.status(HttpStatus.OK).body(FileDto(filePath = filePath))
    }
}
