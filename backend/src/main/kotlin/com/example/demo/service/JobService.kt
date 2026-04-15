package com.example.demo.service

import com.example.demo.domain.JobName
import com.example.demo.domain.JobStatus
import com.example.demo.dto.JobDto
import com.example.demo.kubernetes.K8sJobComponent
import com.example.demo.model.Job
import com.example.demo.repository.JobRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue
import java.util.*

@Service
class JobService(
    private val jobRepository: JobRepository,
    private val k8sJobComponent: K8sJobComponent
) {
    fun findById(id: UUID): JobDto {
        return jobRepository.findById(id)
            .map(this::mapJobToJobDto)
            .orElseThrow { NoSuchElementException("Job not found: $id") }
    }

    fun findByName(
        name: String,
        pageNumber: Int = 0,
        pageSize: Int = 10,
        sortOrder: String = "DESC",
        sortBy: String = "created_at"
    ): Page<JobDto> {
        val direction = Sort.Direction
            .fromOptionalString(sortOrder.uppercase())
            .orElseGet { Sort.Direction.DESC }

        val allowedSortFields = setOf("createdAt", "startAt", "endAt")

        val safeSortBy = if (sortBy in allowedSortFields) sortBy else "createdAt"

        val pageable = PageRequest.of(
            pageNumber,
            pageSize,
            Sort.by(direction, safeSortBy)
        )

        return jobRepository.findByName(name, pageable)
            .map(this::mapJobToJobDto)
    }

     fun createTransformImageJob(filePath: String): UUID = runBlocking {
         coroutineScope {
            val metadataMap = mapOf("filePath" to filePath)

            val jobId = withContext(Dispatchers.IO) {
                createJobRecord(JobName.TRANSFORM_IMAGE.toString(), metadataMap) ?: throw RuntimeException("Failed to create job record")
            }

            launch(Dispatchers.IO) {
                try {
                    k8sJobComponent.triggerTransformImageJob(jobId.toString())
                } catch (ex: Exception) {
                    println("Failed to trigger K8s job for $jobId")
                    ex.printStackTrace()

                    updateJobStatus(jobId, JobStatus.FAILED, "Failed to start job")
                }
            }

            jobId
        }
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

     fun updateJobStatus(
        jobId: UUID,
        status: JobStatus,
        error: String? = null
    ) {
        val job: Job = jobRepository.findById(jobId)
            .orElseThrow { IllegalArgumentException("Job not found: $jobId") }

        job.status = status.code

        if (status == JobStatus.FAILED) {
            job.error = error
        }

        jobRepository.save(job)
    }

    private fun mapJobToJobDto(job: Job): JobDto {
        val statusEnum = JobStatus.fromCode(job.status ?: 0)
        val mapper = jacksonObjectMapper()

        val metadata = mapper.readValue<Map<String, Any>>(job.metadata ?: "{}")
        return JobDto(
            id = job.id,
            name = job.name,
            status = statusEnum?.displayName ?: "Unknown",
            startAt = job.startAt,
            endAt = job.endAt,
            createdAt = job.createdAt,
            error = job.error,
            metadata = metadata,
        )
    }
}
