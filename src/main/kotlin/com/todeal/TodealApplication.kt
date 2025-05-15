package com.todeal

import io.github.cdimascio.dotenv.dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class TodealApplication

fun main(args: Array<String>) {
    // ✅ dotenv 로드
    val dotenv = dotenv()

    // ✅ 환경변수로 등록
    System.setProperty("BOOTPAY_REST_API_KEY", dotenv["BOOTPAY_REST_API_KEY"])
    System.setProperty("BOOTPAY_PRIVATE_KEY", dotenv["BOOTPAY_PRIVATE_KEY"])

    runApplication<TodealApplication>(*args)
}
