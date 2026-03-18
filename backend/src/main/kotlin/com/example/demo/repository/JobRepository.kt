package com.example.demo.repository

import com.example.demo.model.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface JobRepository : CrudRepository<Job, UUID> {
    fun findByName(name: String, pageable: Pageable): Page<Job>
}
