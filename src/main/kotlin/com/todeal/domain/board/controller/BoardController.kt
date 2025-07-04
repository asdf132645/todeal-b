// ✅ BoardController.kt
package com.todeal.domain.board.controller

import com.todeal.domain.board.dto.*
import com.todeal.domain.board.service.BoardService
import com.todeal.global.response.ApiResponse
import com.todeal.global.response.CursorResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("/board")
class BoardController(
    private val boardService: BoardService
) {
    @GetMapping
    fun getPosts(
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
        @RequestParam(required = false) distance: Double?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) keyword: String?,
        @RequestParam(required = false) field: String?,
        @RequestParam(required = false) cursorCreatedAt: String?,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(required = false, defaultValue = "20") size: Int
    ): ApiResponse<CursorResponse<BoardPostResponse>> {
        val parsedCreatedAt = cursorCreatedAt?.let { LocalDateTime.parse(it) }

        return ApiResponse.success(
            boardService.getPosts(
                latitude, longitude, distance, category, keyword, field,
                parsedCreatedAt, cursorId, size
            )
        )
    }


    @GetMapping("/{id}")
    fun getPost(
        @PathVariable id: Long,
        request: HttpServletRequest,
        @RequestHeader(value = "X-USER-ID", required = false) userId: Long?
    ): ApiResponse<BoardPostResponse> {
        val rawIp = request.getHeader("X-Forwarded-For")
        val ip = rawIp?.split(",")?.firstOrNull()?.trim() ?: request.remoteAddr

        val post = boardService.getPost(id, ip, userId)
        return ApiResponse.success(post)
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
        @RequestHeader("X-USER-ID") userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse<Page<BoardPostResponse>> {
        val result = boardService.getMyPosts(userId, page, size)
        return ApiResponse.success(result)
    }
}