// 1. 공통 응답 객체 (src/main/kotlin/com/todeal/global/response/ApiResponse.kt)
package com.todeal.global.response

data class ApiResponse<T>(
    val success: Boolean = true,
    val data: T? = null,
    val message: String? = null
) {
    companion object {
        fun <T> success(data: T): ApiResponse<T> = ApiResponse(data = data)
        fun fail(message: String): ApiResponse<Nothing> = ApiResponse(success = false, data = null, message = message)
    }
}
