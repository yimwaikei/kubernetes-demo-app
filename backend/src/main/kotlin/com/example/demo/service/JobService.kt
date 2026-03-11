package com.example.demo.service

import com.example.demo.domain.JobStatus
import com.example.demo.dto.JobDto
import com.example.demo.model.Job
import com.example.demo.repository.JobRepository
import org.springframework.stereotype.Service

@Service
class JobService(
    private val jobRepository: JobRepository
) {

    fun findByName(name: String): List<JobDto> {
        return jobRepository.findByName(name).map { job -> mapJobToJobDto(job) }
    }

    private fun mapJobToJobDto(job: Job): JobDto {
        val statusEnum = JobStatus.fromCode(job.status)
        return JobDto(
            id = job.id,
            name = job.name,
            status = statusEnum?.displayName ?: "Unknown",
            startAt = job.startAt,
            endAt = job.endAt,
            createdAt = job.createdAt,
        )
    }
}
