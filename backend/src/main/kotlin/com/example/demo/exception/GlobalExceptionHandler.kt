package com.example.demo.exception

import org.apache.coyote.BadRequestException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.NoSuchElementException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException::class, BadRequestException::class)
    fun handle400(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .badRequest()
            .body(ErrorResponse(message = ex.message ?: "Bad Request"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handle404(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(message = ex.message ?: "Not Found"))
    }
}
