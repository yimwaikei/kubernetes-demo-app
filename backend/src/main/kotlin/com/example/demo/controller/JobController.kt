package com.example.demo.controller

import com.example.demo.dto.CreateJobRequest
import com.example.demo.dto.CreateJobResponse
import com.example.demo.dto.JobDto
import com.example.demo.service.JobService
import org.apache.coyote.BadRequestException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/jobs")
class JobController(
    private val jobService: JobService
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

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createJob(@RequestBody request: CreateJobRequest): ResponseEntity<CreateJobResponse> {
        val id = jobService.createTransformImageJob(request.filePath) ?: throw BadRequestException("Fail to create job")
        return ResponseEntity.ok(CreateJobResponse(id))
    }
}
