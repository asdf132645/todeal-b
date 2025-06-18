package com.todeal.domain.file.controller

import com.todeal.global.response.ApiResponse
import com.todeal.infrastructure.s3.S3UploadService
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/upload")
class UploadController(
    private val s3UploadService: S3UploadService
) {
    @PostMapping("/image")
    fun uploadImage(@RequestPart file: MultipartFile): ApiResponse<String> {
        val imageUrl = s3UploadService.upload(file)
        return ApiResponse.success(imageUrl)
    }

    @DeleteMapping("/image")
    fun deleteImage(@RequestParam imageUrl: String): ApiResponse<String> {
        s3UploadService.delete(imageUrl)
        return ApiResponse.success("이미지 삭제 완료")
    }
}
