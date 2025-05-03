package com.todeal.domain.user.controller

import com.todeal.domain.user.dto.*
import com.todeal.domain.user.service.UserService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: UserSignupRequest): ApiResponse<UserResponse> {
        val result = userService.signup(request)
        return ApiResponse.success(result)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: UserLoginRequest): ApiResponse<UserResponse> {
        val result = userService.login(request)
        return ApiResponse.success(result)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UserUpdateRequest): ApiResponse<UserResponse> {
        val result = userService.update(id, request)
        return ApiResponse.success(result)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ApiResponse<UserResponse> {
        val result = userService.getById(id)
        return ApiResponse.success(result)
    }
}
