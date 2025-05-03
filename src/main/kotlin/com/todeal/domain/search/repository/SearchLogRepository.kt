package com.todeal.domain.search.repository

import com.todeal.domain.search.entity.SearchEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SearchRepository : JpaRepository<SearchEntity, Long>
