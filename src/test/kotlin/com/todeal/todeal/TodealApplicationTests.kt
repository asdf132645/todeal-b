package com.todeal.todeal

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TodealApplicationTests {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupEnv() {
            // ✅ .env 값을 수동으로 환경변수에 등록
            System.setProperty("BOOTPAY_REST_API_KEY", "6822d13e00d008657455b08e")
            System.setProperty("BOOTPAY_PRIVATE_KEY", "3m9vdEG1+Wj5lAo7vRJ0ljqUPR3jRE+VcmXxPlpodJc=")
        }
    }

    @Test
    fun contextLoads() {
        // 테스트가 빈 초기화에 실패하지 않도록 보장
    }
}
