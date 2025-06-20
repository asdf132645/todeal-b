package com.todeal.domain.analytics.repository

import com.todeal.domain.analytics.entity.VisitorLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface VisitorLogRepository : JpaRepository<VisitorLogEntity, Long>
