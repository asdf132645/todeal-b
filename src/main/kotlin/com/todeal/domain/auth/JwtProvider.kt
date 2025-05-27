package com.todeal.domain.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct
import javax.crypto.SecretKey

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secret: String
) {
    private lateinit var secretKey: SecretKey

    private val accessTokenValidity = 1000L * 60 * 60           // 1시간
    private val refreshTokenValidity = 1000L * 60 * 60 * 24 * 14 // 2주
    private val tempTokenValidity = 1000L * 60 * 10              // 10분

    @PostConstruct
    fun init() {
        val keyBytes = Decoders.BASE64.decode(secret)
        secretKey = Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateAccessToken(userId: Long): String =
        Jwts.builder()
            .setSubject(userId.toString())
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + accessTokenValidity))
            .signWith(secretKey)
            .compact()

    fun generateRefreshToken(): String =
        Jwts.builder()
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

    fun getUserIdFromToken(token: String): Long {
        val raw = token.replace("Bearer ", "")
        val subject = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(raw)
            .body.subject

        return if (subject.startsWith("KAKAO:")) {
            subject.removePrefix("KAKAO:").toLong()
        } else {
            subject.toLong()
        }
    }

    fun getKakaoIdFromTempToken(token: String): Long {
        val raw = token.replace("Bearer ", "")
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
