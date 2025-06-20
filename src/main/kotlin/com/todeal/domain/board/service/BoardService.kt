package com.todeal.domain.board.service

import com.todeal.domain.board.dto.*
import com.todeal.domain.board.entity.BoardCommentEntity
import com.todeal.domain.board.entity.BoardPostEntity
import com.todeal.domain.board.repository.BoardCommentRepository
import com.todeal.domain.board.repository.BoardPostRepository
import com.todeal.global.redis.RedisCacheService
import com.todeal.global.response.CursorResponse
import com.todeal.infrastructure.s3.S3UploadService
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import java.time.LocalDateTime
import kotlin.math.*

@Service
class BoardService(
    private val boardPostRepository: BoardPostRepository,
    private val boardCommentRepository: BoardCommentRepository,
    private val redisCacheService: RedisCacheService,
    private val s3UploadService: S3UploadService,

    ) {

    fun getPosts(
        latitude: Double?,
        longitude: Double?,
        distance: Double?,
        category: String?,
        keyword: String?,
        field: String?,
        cursorCreatedAt: LocalDateTime?,
        cursorId: Long?,
        size: Int
    ): CursorResponse<BoardPostResponse> {

        val allMode = cursorCreatedAt == null && cursorId == null && size == Int.MAX_VALUE
        val hasCursor = !allMode && cursorCreatedAt != null && cursorId != null
        val safeCreatedAt = cursorCreatedAt ?: LocalDateTime.of(9999, 12, 31, 23, 59, 59)
        val safeCursorId = cursorId ?: Long.MAX_VALUE
        val limitSize = if (allMode) Int.MAX_VALUE else size
        val fetchSize = if (allMode) Int.MAX_VALUE else size + 1 // ✅ limit+1

        val isFilteredCategory = !category.isNullOrBlank() && category.lowercase() != "all"

        val posts = if (latitude != null && longitude != null && distance != null) {
            val earthRadius = 6371.0
            val latDelta = Math.toDegrees(distance / earthRadius)
            val lngDelta = Math.toDegrees(distance / earthRadius / cos(Math.toRadians(latitude)))

            val latMin = latitude - latDelta
            val latMax = latitude + latDelta
            val lngMin = longitude - lngDelta
            val lngMax = longitude + lngDelta

            when {
                isFilteredCategory && keyword != null && field != null ->
                    boardPostRepository.findWithinDistanceCategoryAndKeyword(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        category!!, keyword, field,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                isFilteredCategory ->
                    boardPostRepository.findWithinDistanceAndCategory(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        category!!,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                keyword != null && field != null ->
                    boardPostRepository.findWithinDistanceAndKeyword(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        keyword, field,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                else ->
                    boardPostRepository.findWithinDistance(
                        latitude, longitude,
                        latMin, latMax, lngMin, lngMax, distance,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
            }
        } else {
            when {
                isFilteredCategory && keyword != null && field != null ->
                    boardPostRepository.findByCategoryAndKeyword(
                        category!!, keyword, field,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                isFilteredCategory ->
                    boardPostRepository.findByCategoryOrderByCreatedAtDesc(
                        category!!,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                keyword != null && field != null ->
                    boardPostRepository.findByKeyword(
                        field, keyword,
                        hasCursor, safeCreatedAt, safeCursorId, fetchSize
                    )
                else -> {
                    if (hasCursor) {
                        boardPostRepository.findAllByCursorWithCursor(
                            safeCreatedAt,
                            safeCursorId,
                            fetchSize
                        )
                    } else {
                        boardPostRepository.findAllByCursorNoCursor(
                            fetchSize
                        )
                    }
                }

            }
        }
        println("💥 FETCHED ${posts.size}, LIMIT=$fetchSize")

        val hasNextPage = posts.size > limitSize
        val nextCursorTarget = if (hasNextPage) posts[limitSize] else null
        val slicedPosts = if (hasNextPage) posts.subList(0, limitSize) else posts

        return CursorResponse(
            items = slicedPosts.map { BoardPostResponse.from(it) },
            nextCursorId = nextCursorTarget?.id,
            nextCursorCreatedAt = nextCursorTarget?.createdAt?.toString(),
            hasNext = hasNextPage
        )



    }



    @Transactional
    fun getPost(id: Long, viewerIp: String?, userId: Long?): BoardPostResponse {
        val post = boardPostRepository.findById(id)
            .orElseThrow { IllegalArgumentException("게시글이 존재하지 않습니다.") }

        val key = when {
            userId != null -> "view:post:$id:user:$userId"
            viewerIp != null -> "view:post:$id:ip:$viewerIp"
            else -> null
        }

        if (key != null && redisCacheService.isFirstView(key, 3600)) {
            post.viewCount += 1
        }

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

        if (post.imageUrls.isNotEmpty()) {
            s3UploadService.deleteAll(post.imageUrls)
        }

        // ✅ 게시글 삭제
        boardPostRepository.delete(post)
    }

    @Transactional
    fun createComment(userId: Long, request: BoardCommentRequest): BoardCommentResponse {
        val comment = BoardCommentEntity(
            postId = request.postId,
            userId = userId,
            content = request.content,
            nickname = request.nickname,
//            language = request.language,
            translatedContent = null
        )
        val saved = boardCommentRepository.save(comment)
        return BoardCommentResponse.from(saved)
    }

    fun getComments(postId: Long): List<BoardCommentResponse> {
        return boardCommentRepository.findByPostId(postId)
            .map { BoardCommentResponse.from(it) }
    }

    fun getMyPosts(userId: Long, page: Int, size: Int): Page<BoardPostResponse> {
        val pageable: Pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return boardPostRepository.findByUserId(userId, pageable)
            .map { BoardPostResponse.from(it) }
    }


}
