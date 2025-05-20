package com.todeal.domain.customerSupport.repository

import com.todeal.domain.customerSupport.entity.CustomerSupportEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CustomerSupportRepository : JpaRepository<CustomerSupportEntity, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<CustomerSupportEntity>
}
