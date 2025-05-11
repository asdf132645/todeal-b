package com.todeal.domain.push.entity

import jakarta.persistence.*

@Entity
@Table(name = "fcm_token")
data class FcmTokenEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val fcmToken: String
)
