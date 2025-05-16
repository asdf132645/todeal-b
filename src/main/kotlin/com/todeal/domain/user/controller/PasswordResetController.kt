package com.todeal.domain.user.controller

import com.todeal.domain.user.dto.PasswordResetConfirmRequest
import com.todeal.domain.user.dto.PasswordResetRequest
import com.todeal.domain.user.service.PasswordResetService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/userAuth")
class PasswordResetController(
    private val passwordResetService: PasswordResetService
) {

    @PostMapping("/reset-password-request")
    fun requestReset(@RequestBody request: PasswordResetRequest): ApiResponse<String> {
        passwordResetService.sendResetEmail(request.email)
        return ApiResponse.success("비밀번호 재설정 이메일이 발송되었습니다.")
    }

    @PostMapping("/reset-password")
    fun confirmReset(@RequestBody request: PasswordResetConfirmRequest): ApiResponse<String> {
        passwordResetService.resetPassword(request.token, request.newPassword)
        return ApiResponse.success("비밀번호가 변경되었습니다.")
    }
}
