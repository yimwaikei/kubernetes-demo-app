package com.example.demo.dto

import com.fasterxml.jackson.annotation.JsonAnyGetter
import java.time.Instant
import java.util.UUID

data class JobDto(
    val id: UUID?,
    val name: String,
    val status: String,
    val startAt: Instant?,
    val endAt: Instant?,
    val createdAt: Instant,
    val error: String?,
    private val metadata: Map<String, Any> = emptyMap()
) {
    @JsonAnyGetter
    fun getMetadata(): Map<String, Any> = metadata
}

data class CreateJobRequest(
    val filePath: String,
)

data class CreateJobResponse(
    val id: UUID,
)
