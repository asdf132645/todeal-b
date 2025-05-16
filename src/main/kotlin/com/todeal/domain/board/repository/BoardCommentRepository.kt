// ✅ BoardCommentRepository.kt
package com.todeal.domain.board.repository

import com.todeal.domain.board.entity.BoardCommentEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BoardCommentRepository : JpaRepository<BoardCommentEntity, Long> {
    fun findByPostId(postId: Long): List<BoardCommentEntity>
}
