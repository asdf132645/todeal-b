package com.todeal.domain.payment.service

import com.todeal.domain.payment.entity.PaymentEntity
import com.todeal.domain.payment.repository.PaymentRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import java.time.LocalDateTime

@Service
class BootpayService(
    private val builder: WebClient.Builder,
    private val paymentRepository: PaymentRepository
) {
    @Value("\${bootpay.rest-api-key}")
    lateinit var restApiKey: String

    @Value("\${bootpay.private-key}")
    lateinit var privateKey: String

    fun getAccessToken(): String {
        val client = builder.baseUrl("https://api.bootpay.co.kr").build()

        val response = client.post()
            .uri("/request/token")
            .bodyValue(
                mapOf(
                    "application_id" to restApiKey,
                    "private_key" to privateKey
                )
            )
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val data = response?.get("data") as? Map<*, *>
            ?: throw RuntimeException("응답 오류: data 필드 누락")

        return data["token"] as? String
            ?: throw RuntimeException("토큰 없음")
    }

    fun verifyReceipt(receiptId: String, userId: Long): Boolean {
        val token = getAccessToken()

        val client = builder.baseUrl("https://api.bootpay.co.kr").build()

        val response = client.get()
            .uri("/receipt/$receiptId")
            .header("Authorization", token)
            .retrieve()
            .bodyToMono(Map::class.java)
            .block()

        val data = response?.get("data") as? Map<*, *> ?: return false

        val status = data["status"]?.toString()
        val price = data["price"]?.toString()?.toIntOrNull()
        val method = data["method"]?.toString()

        if (status == "done" && price != null) {
            val payment = PaymentEntity(
                userId = userId,
                amount = price,
                type = "bootpay",
                method = method,
                receiptId = receiptId,
                status = "success",
                createdAt = LocalDateTime.now()
            )
            paymentRepository.save(payment)
            return true
        }
        return false
    }
}
