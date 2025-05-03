package com.todeal.domain.deal.repository

import com.todeal.domain.deal.entity.DealEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DealRepository : JpaRepository<DealEntity, Long>
