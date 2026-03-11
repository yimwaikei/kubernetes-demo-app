package com.example.demo.model

import jakarta.persistence.*
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "jobs")
data class Job(
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    val name: String = "",

    @Column(columnDefinition = "jsonb")
    val metadata: String = "{}",

    @Column
    val status: Short = 0,

    @Column(name = "start_at")
    val startAt: Instant? = null,

    @Column(name = "end_at")
    val endAt: Instant? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
