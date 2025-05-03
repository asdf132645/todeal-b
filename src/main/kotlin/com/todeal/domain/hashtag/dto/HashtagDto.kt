package com.todeal.domain.hashtag.dto

import com.todeal.domain.hashtag.entity.HashtagEntity

data class HashtagDto(
    val id: Long,
    val name: String
) {
    companion object {
        fun from(entity: HashtagEntity): HashtagDto {
            return HashtagDto(
                id = entity.id,
                name = entity.name
            )
        }
    }
}
