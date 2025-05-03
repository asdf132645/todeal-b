package com.todeal.domain.hashtag.repository

import com.todeal.domain.hashtag.entity.HashtagEntity
import org.springframework.data.jpa.repository.JpaRepository

interface HashtagRepository : JpaRepository<HashtagEntity, Long> {
    fun findByName(name: String): HashtagEntity?
}
