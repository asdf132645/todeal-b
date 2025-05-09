// ✅ com/todeal/domain/deal/repository/DealRepositoryExtensions.kt

package com.todeal.domain.deal.repository

import com.todeal.domain.deal.entity.DealEntity

fun DealRepository.getByIdOrThrow(id: Long): DealEntity {
    return this.findById(id).orElseThrow {
        NoSuchElementException("💥 Deal (id=$id) 이 존재하지 않습니다.")
    }
}
