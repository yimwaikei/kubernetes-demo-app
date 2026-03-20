package com.example.demo.controller

import com.example.demo.dto.FileDto
import com.example.demo.dto.JobDto
import com.example.demo.dto.ListOfFilesResponse
import com.example.demo.service.FileService
import com.example.demo.service.JobService
import io.minio.MinioClient
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private val fileService: FileService,
) {
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadFile(@RequestParam("file") file: MultipartFile): ResponseEntity<FileDto> {
        val filePath = fileService.uploadFile(file)
        return ResponseEntity.status(HttpStatus.OK).body(FileDto(filePath = filePath))
    }

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun listOfFiles(
        @RequestParam("folder") folder: String = "",
    ): ResponseEntity<ListOfFilesResponse> {
        val files = fileService.listOfFilesInFolder(folder)
        return ResponseEntity.status(HttpStatus.OK).body(ListOfFilesResponse(files))
    }

    @GetMapping("/download")
    fun getDownloadUrl(
        @RequestParam("filePath") filePath: String = "",
    ): ResponseEntity<InputStreamResource> {
        val file = fileService.downloadFile(filePath)

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=\"${file.fileName}\"")
            .contentType(MediaType.parseMediaType(file.contentType))
            .body(InputStreamResource(file.stream))
    }
}
