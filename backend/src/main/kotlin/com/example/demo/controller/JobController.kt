package com.example.demo.controller

import com.example.demo.dto.JobDto
import com.example.demo.service.JobService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/jobs")
class JobController (
    private val jobService: JobService
) {
    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun listOfJobs(@RequestParam("name") name: String = ""): ResponseEntity<List<JobDto>> {
        val jobs = jobService.findByName(name)
        return ResponseEntity.status(HttpStatus.OK).body(jobs)
    }
}
