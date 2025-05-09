package com.todeal.infrastructure.fcm

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.File
import java.io.InputStream

@Component
class FirebaseCredentialLoader(
    @Value("\${firebase.credentials.path}") private val credentialPath: String
) {

    fun load(): InputStream {
        // 1. 파일 시스템 (개발 환경)
        val devFile = File(credentialPath)
        if (devFile.exists()) {
            println("📄 Firebase 인증 키 (파일 시스템)에서 로드됨: $credentialPath")
            return devFile.inputStream()
        }

        // 2. classpath (운영 환경)
        val classpathStream = javaClass.getResourceAsStream("/$credentialPath")
        if (classpathStream != null) {
            println("📦 Firebase 인증 키 (classpath)에서 로드됨: /$credentialPath")
            return classpathStream
        }

        // 3. 실패
        throw IllegalStateException("❌ Firebase 인증 키를 찾을 수 없습니다: $credentialPath")
    }
}
