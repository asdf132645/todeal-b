package com.todeal.domain.board.service

import com.todeal.domain.board.dto.*
import com.todeal.domain.board.entity.BoardCommentEntity
import com.todeal.domain.board.entity.BoardPostEntity
import com.todeal.domain.board.repository.BoardCommentRepository
import com.todeal.domain.board.repository.BoardPostRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.*

@Service
class BoardService(
    private val boardPostRepository: BoardPostRepository,
    private val boardCommentRepository: BoardCommentRepository
) {

    fun getPosts(
        latitude: Double?,
        longitude: Double?,
        distance: Double?,
        category: String?,
        keyword: String?,
        field: String?
    ): List<BoardPostResponse> {
        val posts = if (latitude != null && longitude != null && distance != null) {
            val earthRadius = 6371.0
            val latDelta = Math.toDegrees(distance / earthRadius)
            val lngDelta = Math.toDegrees(distance / earthRadius / cos(Math.toRadians(latitude)))

            val latMin = latitude - latDelta
            val latMax = latitude + latDelta
            val lngMin = longitude - lngDelta
            val lngMax = longitude + lngDelta

            when {
                category != null && keyword != null && field != null ->
                    boardPostRepository.findWithinDistanceCategoryAndKeyword(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        category, keyword, field
                    )
                category != null ->
                    boardPostRepository.findWithinDistanceAndCategory(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        category
                    )
                keyword != null && field != null ->
                    boardPostRepository.findWithinDistanceAndKeyword(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        keyword, field
                    )
                else ->
                    boardPostRepository.findWithinDistance(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance
                    )
            }
        } else {
            when {
                category != null && keyword != null && field != null ->
                    boardPostRepository.findByCategoryAndKeyword(category, keyword, field)

                category != null ->
                    boardPostRepository.findByCategoryOrderByCreatedAtDesc(category)

                keyword != null && field != null ->
                    boardPostRepository.findByKeyword(field, keyword)

                else ->
                    boardPostRepository.findTop100ByOrderByCreatedAtDesc()
            }
        }

        return posts.map { BoardPostResponse.from(it) }
    }

    fun getPost(id: Long): BoardPostResponse {
        val post = boardPostRepository.findById(id)
            .orElseThrow { IllegalArgumentException("게시글이 존재하지 않습니다.") }
        return BoardPostResponse.from(post)
    }

    @Transactional
    fun createPost(userId: Long, request: BoardPostRequest): BoardPostResponse {
        val post = BoardPostEntity(
            userId = userId,
            title = request.title,
            content = request.content,
            category = request.category,
            nickname = request.nickname,
            latitude = request.latitude,
            longitude = request.longitude,
            region = request.region,
            language = request.language,
            translatedTitle = request.translatedTitle,
            translatedContent = request.translatedContent,
            imageUrls = request.imageUrls // ✅ 추가
        )
        val saved = boardPostRepository.save(post)
        return BoardPostResponse.from(saved)
    }

    @Transactional
    fun updatePost(userId: Long, postId: Long, request: BoardPostRequest): BoardPostResponse {
        val post = boardPostRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("게시글이 존재하지 않습니다.") }

        if (post.userId != userId) throw IllegalAccessException("수정 권한이 없습니다.")

        post.apply {
            title = request.title
            content = request.content
            category = request.category
            latitude = request.latitude
            longitude = request.longitude
            region = request.region
            nickname = request.nickname
            language = request.language
            translatedTitle = request.translatedTitle
            translatedContent = request.translatedContent
            imageUrls = request.imageUrls // ✅ 추가
            updatedAt = java.time.LocalDateTime.now()
        }

        return BoardPostResponse.from(post)
    }

    @Transactional
    fun deletePost(userId: Long, postId: Long) {
        val post = boardPostRepository.findById(postId)
            .orElseThrow { IllegalArgumentException("게시글이 존재하지 않습니다.") }

        if (post.userId != userId) throw IllegalAccessException("삭제 권한이 없습니다.")
        boardPostRepository.delete(post)
    }

    @Transactional
    fun createComment(userId: Long, request: BoardCommentRequest): BoardCommentResponse {
        val comment = BoardCommentEntity(
            postId = request.postId,
            userId = userId,
            content = request.content,
            nickname = request.nickname,
            language = request.language,
            translatedContent = null
        )
        val saved = boardCommentRepository.save(comment)
        return BoardCommentResponse.from(saved)
    }

    fun getComments(postId: Long): List<BoardCommentResponse> {
        return boardCommentRepository.findByPostId(postId)
            .map { BoardCommentResponse.from(it) }
    }

    fun getMyPosts(userId: Long): List<BoardPostResponse> {
        return boardPostRepository.findByUserId(userId)
            .map { BoardPostResponse.from(it) }
    }
}
