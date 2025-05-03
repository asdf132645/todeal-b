package com.todeal.domain.hashtag.entity

import jakarta.persistence.*

@Entity
@Table(name = "hashtags")
data class HashtagEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val name: String
)
