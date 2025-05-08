// ✅ UserLocationService.kt
package com.todeal.domain.userlocation

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserLocationService(
    private val userLocationRepository: UserLocationRepository
) {

    @Transactional
    fun upsertUserLocation(userId: Long, request: LocationSaveRequest) {
        userLocationRepository.upsertLocation(
            userId = userId,
            lat = request.latitude,
            lng = request.longitude
        )
    }

    fun getUserLocation(userId: Long): UserLocationEntity {
        return userLocationRepository.findByUserId(userId)
            ?: throw IllegalArgumentException("위치 정보가 존재하지 않습니다. userId=$userId")
    }
}
