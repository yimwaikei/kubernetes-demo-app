package com.example.demo.domain

enum class JobStatus(val code: Short, val displayName: String) {
    CREATED(0, "Created"),
    RUNNING(1, "Running"),
    COMPLETED(2, "Completed"),
    FAILED(3, "Failed");

    companion object {
        fun fromCode(code: Short) = entries.firstOrNull { it.code == code }
    }
}
