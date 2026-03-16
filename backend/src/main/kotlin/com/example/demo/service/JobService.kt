package com.example.demo.service

import com.example.demo.domain.JobName
import com.example.demo.domain.JobStatus
import com.example.demo.dto.JobDto
import com.example.demo.model.Job
import com.example.demo.repository.JobRepository
import org.springframework.stereotype.Service
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.util.UUID

@Service
class JobService(
    private val jobRepository: JobRepository
) {

    fun findByName(name: String): List<JobDto> {
        return jobRepository.findByName(name).map { job -> mapJobToJobDto(job) }
    }

    fun createTransformImageJob(filePath: String): UUID? {
        val metadataMap: Map<String, Any> = mapOf("filePath" to filePath)

        // TODO trigger fire & forget kubernetes job (python script to transform image)

        return createJobRecord(JobName.TRANSFORM_IMAGE.toString(), metadataMap)
    }

    private fun createJobRecord(name: String, metadataMap: Map<String, Any>): UUID? {
        val mapper = jacksonObjectMapper()
        val metadataJson = mapper.writeValueAsString(metadataMap)

        return jobRepository.save(
            Job(
                name = name,
                metadata = metadataJson
            )
        ).id
    }

    private fun mapJobToJobDto(job: Job): JobDto {
        val statusEnum = JobStatus.fromCode(job.status ?: 0)
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
