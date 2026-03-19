package com.example.demo.controller

import com.example.demo.dto.CreateJobRequest
import com.example.demo.dto.CreateJobResponse
import com.example.demo.dto.FileDto
import com.example.demo.dto.JobDto
import com.example.demo.service.FileService
import com.example.demo.service.JobService
import org.apache.coyote.BadRequestException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*


@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val jobService: JobService,
    private val fileService: FileService,
) {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun listOfJobs(
        @RequestParam("name") name: String = "",
        @RequestParam("pageNumber") pageNumber: Int = 1,
        @RequestParam("pageSize") pageSize: Int = 10,
        @RequestParam("sortOrder") sortOrder: String = "DESC",
        @RequestParam("sortBy") sortBy: String = "createdAt"
    ): ResponseEntity<Page<JobDto>> {
        val jobs = jobService.findByName(name, pageNumber - 1, pageSize, sortOrder, sortBy)
        return ResponseEntity.status(HttpStatus.OK).body(jobs)
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getJobById(@PathVariable id: UUID): ResponseEntity<JobDto> {
        val job = jobService.findById(id)
        return ResponseEntity.ok(job)
    }

    @GetMapping("/{id}/download", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getJobProcessedFileUrl(
        @PathVariable id: UUID,
        @RequestParam("processed") processed: Boolean = false,
    ): ResponseEntity<FileDto> {
        val job = jobService.findById(id)
        val filePath = job.getMetadata()[if (processed) "processedFilePath" else "filePath"]?.toString() ?: ""
        val downloadUrl = fileService.generatePresignedUrl(filePath)
        return ResponseEntity.ok(
            FileDto(
                filePath = downloadUrl
            )
        )
    }

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createJob(@RequestBody request: CreateJobRequest): ResponseEntity<CreateJobResponse> {
        val id = jobService.createTransformImageJob(request.filePath) ?: throw BadRequestException("Fail to create job")
        return ResponseEntity.ok(CreateJobResponse(id))
    }

    @PostMapping("/{id}/rerun", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun rerunJob(@PathVariable id: UUID): ResponseEntity<CreateJobResponse> {
        val job = jobService.findById(id)
        val id = jobService.createTransformImageJob(job.getMetadata()["filePath"]?.toString() ?: "")
            ?: throw BadRequestException("Fail to create job")
        return ResponseEntity.ok(CreateJobResponse(id))
    }
}
