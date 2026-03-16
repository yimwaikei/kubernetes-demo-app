package com.example.demo.model

import jakarta.persistence.*
import org.hibernate.annotations.ColumnTransformer
import java.time.Instant
import java.util.UUID
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.Type

@DynamicInsert
@Entity
@Table(name = "jobs")
data class Job(
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid", insertable = false, updatable = false)
    val id: UUID? = null,

    @Column(nullable = false, length = 255)
    val name: String = "",

    @Column(columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    val metadata: String? = null,

    @Column
    val status: Short? = null,

    @Column(name = "start_at")
    val startAt: Instant? = null,

    @Column(name = "end_at")
    val endAt: Instant? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    val createdAt: Instant = Instant.now(),
)
