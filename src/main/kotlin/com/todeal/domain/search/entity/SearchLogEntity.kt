package com.todeal.domain.search.entity

import jakarta.persistence.*

@Entity
@Table(name = "searches")
data class SearchEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val keyword: String,

    @Column(name = "user_id")
    val userId: Long? = null
)
