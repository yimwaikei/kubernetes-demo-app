package com.example.demo.dto

import java.time.Instant
import java.util.UUID

data class JobDto(
    val id: UUID?,
    val name: String,
    val status: String,
    val startAt: Instant?,
    val endAt: Instant?,
    val createdAt: Instant
)
