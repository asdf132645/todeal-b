// repository/PaymentRepository.kt
package com.todeal.domain.payment.repository

import com.todeal.domain.payment.entity.Payment
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long>
