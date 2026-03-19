package com.example.demo.dto

data class FileDto(
    val filePath: String,
    val fileName: String? = null,
)

data class ListOfFilesResponse(
    val files: List<FileDto>,
)
