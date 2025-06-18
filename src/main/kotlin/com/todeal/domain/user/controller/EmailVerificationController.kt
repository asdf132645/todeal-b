package com.todeal.domain.user.controller

import com.todeal.domain.user.dto.*
import com.todeal.domain.user.service.EmailVerificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import com.todeal.global.response.ApiResponse

@RestController
@RequestMapping("/emailVerification")
class EmailVerificationController(
    private val emailVerificationService: EmailVerificationService
) {

    @PostMapping("/send-verification")
    fun sendVerification(@RequestBody request: EmailVerificationRequest): ApiResponse<String> {
        emailVerificationService.sendVerificationCode(request.email)
        return ApiResponse.success("인증코드가 이메일로 발송되었습니다.")
    }

    @PostMapping("/verify-code")
    fun verifyCode(@RequestBody request: EmailVerificationCodeRequest): ApiResponse<String> {
        emailVerificationService.verifyCode(request.email, request.code)
        return ApiResponse.success("인증이 완료되었습니다.")
    }
}
