// 2. 전역 예외 처리기 (src/main/kotlin/com/todeal/global/handler/GlobalExceptionHandler.kt)
package com.todeal.global.handler

import com.todeal.global.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.fail(e.message ?: "Internal Server Error"))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.fail(e.message ?: "Invalid request"))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.fail(e.message ?: "Not Found"))
    }
}
