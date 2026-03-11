package com.example.demo.repository

import com.example.demo.model.Job
import org.springframework.data.repository.CrudRepository

interface JobRepository : CrudRepository<Job, Long> {
    fun findByName(name: String): List<Job>
}
