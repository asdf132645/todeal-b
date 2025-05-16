// ✅ BoardController.kt
package com.todeal.domain.board.controller

import com.todeal.domain.board.dto.*
import com.todeal.domain.board.service.BoardService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/board")
class BoardController(
    private val boardService: BoardService
) {
    @GetMapping
    fun getPosts(
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
        @RequestParam(required = false) distance: Double?
    ): ApiResponse<List<BoardPostResponse>> {
        return ApiResponse.success(boardService.getPosts(latitude, longitude, distance))
    }

    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): ApiResponse<BoardPostResponse> {
        return ApiResponse.success(boardService.getPost(id))
    }

    @PostMapping
    fun createPost(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: BoardPostRequest
    ): ApiResponse<BoardPostResponse> {
        return ApiResponse.success(boardService.createPost(userId, request))
    }

    @PatchMapping("/{id}")
    fun updatePost(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long,
        @RequestBody request: BoardPostRequest
    ): ApiResponse<BoardPostResponse> {
        return ApiResponse.success(boardService.updatePost(userId, id, request))
    }

    @DeleteMapping("/{id}")
    fun deletePost(
        @RequestHeader("X-USER-ID") userId: Long,
        @PathVariable id: Long
    ): ApiResponse<String> {
        boardService.deletePost(userId, id)
        return ApiResponse.success("삭제 완료")
    }

    @PostMapping("/comments")
    fun createComment(
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestBody request: BoardCommentRequest
    ): ApiResponse<BoardCommentResponse> {
        return ApiResponse.success(boardService.createComment(userId, request))
    }

    @GetMapping("/{postId}/comments")
    fun getComments(@PathVariable postId: Long): ApiResponse<List<BoardCommentResponse>> {
        return ApiResponse.success(boardService.getComments(postId))
    }

    @GetMapping("/mine")
    fun getMyPosts(
        @RequestHeader("X-USER-ID") userId: Long
    ): ApiResponse<List<BoardPostResponse>> {
        return ApiResponse.success(boardService.getMyPosts(userId))
    }
}