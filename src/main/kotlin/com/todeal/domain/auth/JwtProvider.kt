package com.todeal.domain.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtProvider {
    private val secretKey: SecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256)
    private val accessTokenValidity = 1000L * 60 * 60           // 1시간
    private val refreshTokenValidity = 1000L * 60 * 60 * 24 * 14 // 2주
    private val tempTokenValidity = 1000L * 60 * 10              // 10분

    fun generateAccessToken(userId: Long): String =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenValidity))
            .signWith(secretKey)
            .compact()

    fun generateRefreshToken(userId: Long): String =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + refreshTokenValidity))
            .signWith(secretKey)
            .compact()

    fun generateTempToken(kakaoId: Long): String =
        Jwts.builder()
            .setSubject("KAKAO:$kakaoId")
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + tempTokenValidity))
            .signWith(secretKey)
            .compact()

    // ✅ 모든 토큰에서 userId 반환 (KAKAO 토큰도 여기서 처리)
    fun getUserIdFromToken(token: String): Long {
        val raw = token.replace("Bearer ", "")

//        if (raw.count { it == '.' } != 2) {
//            throw IllegalArgumentException("JWT 형식이 올바르지 않습니다.")
//        }

        val subject = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(raw)
            .body.subject

        // ✅ "KAKAO:12345678" → 12345678
        return if (subject.startsWith("KAKAO:")) {
            subject.removePrefix("KAKAO:").toLong()
        } else {
            subject.toLong()
        }
    }

    // ✅ 필요 시 따로 Kakao ID만 추출하는 함수도 분리 가능
    fun getKakaoIdFromTempToken(token: String): Long {
        val raw = token.replace("Bearer ", "")

//        if (raw.count { it == '.' } != 2) {
//            throw IllegalArgumentException("JWT 형식이 올바르지 않습니다.")
//        }

        val subject = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(raw)
            .body.subject

        if (!subject.startsWith("KAKAO:")) {
            throw IllegalArgumentException("유효한 카카오 임시 토큰이 아닙니다.")
        }

        return subject.removePrefix("KAKAO:").toLong()
    }
}
