package com.todeal.domain.user.repository

import com.todeal.domain.user.entity.UserAgreementEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserAgreementRepository : JpaRepository<UserAgreementEntity, Long> {

    // 특정 사용자 모든 동의 내역 조회
    fun findByUserId(userId: Long): List<UserAgreementEntity>

    // 특정 약관 타입 동의 여부
    fun existsByUserIdAndType(userId: Long, type: String): Boolean

    // 특정 동의 타입에 동의한 전체 유저 ID 조회 (ex. 마케팅 대상자)
    fun findAllByType(type: String): List<UserAgreementEntity>
}
