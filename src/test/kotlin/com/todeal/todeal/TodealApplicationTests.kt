package com.todeal.todeal

import io.github.cdimascio.dotenv.Dotenv
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TodealApplicationTests {

    companion object {
        @JvmStatic
        @BeforeAll
        fun loadEnv() {
            val dotenv = Dotenv.configure()
                .directory("./") // 루트 경로
                .ignoreIfMissing()
                .load()

            System.setProperty("BOOTPAY_REST_API_KEY", dotenv["BOOTPAY_REST_API_KEY"])
            System.setProperty("BOOTPAY_PRIVATE_KEY", dotenv["BOOTPAY_PRIVATE_KEY"])
            System.setProperty("AWS_ACCESS_KEY_ID", dotenv["AWS_ACCESS_KEY_ID"])
            System.setProperty("AWS_SECRET_ACCESS_KEY", dotenv["AWS_SECRET_ACCESS_KEY"])
            System.setProperty("AWS_S3_BUCKET", dotenv["AWS_S3_BUCKET"])
            System.setProperty("cloud.aws.credentials.access-key", dotenv["AWS_ACCESS_KEY_ID"])
            System.setProperty("cloud.aws.credentials.secret-key", dotenv["AWS_SECRET_ACCESS_KEY"])
            System.setProperty("cloud.aws.region.static", dotenv["AWS_REGION"] ?: "ap-northeast-2")
        }
    }

    @Test
    fun contextLoads() {
        println("✅ BOOTPAY KEY: " + System.getProperty("BOOTPAY_REST_API_KEY"))
        println("✅ AWS KEY: " + System.getProperty("AWS_ACCESS_KEY_ID"))
    }
}
