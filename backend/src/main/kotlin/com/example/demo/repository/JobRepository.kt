package com.example.demo.repository

import com.example.demo.model.Job
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository

interface JobRepository : CrudRepository<Job, Long> {
    fun findByName(name: String, pageable: Pageable): Page<Job>
}
