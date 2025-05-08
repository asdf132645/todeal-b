// ✅ UserLocationEntity.kt
package com.todeal.domain.userlocation

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "user_location")
data class UserLocationEntity(

    @Id
    @Column(name = "user_id")
    val userId: Long,

    @Column(nullable = false)
    val latitude: Double,

    @Column(nullable = false)
    val longitude: Double,

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

