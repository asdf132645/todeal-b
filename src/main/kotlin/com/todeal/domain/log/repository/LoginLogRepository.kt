package com.todeal.domain.log.repository

import com.todeal.domain.log.entity.LoginLogEntity
import org.springframework.data.jpa.repository.JpaRepository

interface LoginLogRepository : JpaRepository<LoginLogEntity, Long>
