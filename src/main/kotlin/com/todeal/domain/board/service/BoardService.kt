// ✅ BoardService.kt
package com.todeal.domain.board.service

import com.todeal.domain.board.dto.*
import com.todeal.domain.board.entity.*
import com.todeal.domain.board.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoardService(
    private val boardPostRepository: BoardPostRepository,
    private val boardCommentRepository: BoardCommentRepository
) {
    fun getPosts(latitude: Double?, longitude: Double?, distance: Double?): List<BoardPostResponse> {
        val posts = if (latitude != null && longitude != null && distance != null) {
            boardPostRepository.findWithinDistance(latitude, longitude, distance)
        } else {
            boardPostRepository.findTop100ByOrderByCreatedAtDesc()
        }
        return posts.map(BoardPostResponse::from)
    }

    fun getPost(id: Long): BoardPostResponse {
        val post = boardPostRepository.findById(id).orElseThrow { RuntimeException("게시글 없음") }
        post.viewCount += 1
        return BoardPostResponse.from(boardPostRepository.save(post))
    }


    fun createPost(userId: Long, request: BoardPostRequest): BoardPostResponse {
        val post = boardPostRepository.save(
            BoardPostEntity(
                userId = userId,
                title = request.title,
                content = request.content,
                latitude = request.latitude,
                longitude = request.longitude,
                nickname = request.nickname,
                region = request.region
            )
        )
        return BoardPostResponse.from(post)
    }

    @Transactional
    fun updatePost(userId: Long, postId: Long, request: BoardPostRequest): BoardPostResponse {
        val post = boardPostRepository.findById(postId).orElseThrow { RuntimeException("게시글 없음") }
        if (post.userId != userId) throw RuntimeException("수정 권한 없음")
        val updated = post.copy(
            title = request.title,
            content = request.content,
            updatedAt = java.time.LocalDateTime.now()
        )
        return BoardPostResponse.from(boardPostRepository.save(updated))
    }

    @Transactional
    fun deletePost(userId: Long, postId: Long) {
        val post = boardPostRepository.findById(postId).orElseThrow { RuntimeException("게시글 없음") }
        if (post.userId != userId) throw RuntimeException("삭제 권한 없음")
        boardPostRepository.delete(post)
    }

    @Transactional
    fun createComment(userId: Long, request: BoardCommentRequest): BoardCommentResponse {
        val comment = boardCommentRepository.save(
            BoardCommentEntity(
                userId = userId,
                postId = request.postId,
                content = request.content,
                nickname = request.nickname
            )
        )
        val post = boardPostRepository.findById(request.postId).orElseThrow()
        post.commentCount += 1
        return BoardCommentResponse.from(comment)
    }

    fun getComments(postId: Long): List<BoardCommentResponse> {
        return boardCommentRepository.findByPostId(postId).map(BoardCommentResponse::from)
    }

    fun getMyPosts(userId: Long): List<BoardPostResponse> {
        return boardPostRepository.findByUserId(userId).map(BoardPostResponse::from)
    }
}
