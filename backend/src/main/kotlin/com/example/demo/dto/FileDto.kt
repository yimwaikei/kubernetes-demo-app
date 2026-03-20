package com.example.demo.dto

import java.io.InputStream

data class FileDto(
    val filePath: String,
    val fileName: String? = null,
)

data class ListOfFilesResponse(
    val files: List<FileDto>,
)

data class FileDownloadResponse(
    val stream: InputStream,
    val fileName: String,
    val contentType: String = "application/octet-stream"
)
