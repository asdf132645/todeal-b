package com.todeal.global.security

import com.todeal.domain.auth.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    private fun isExcluded(request: HttpServletRequest): Boolean {
        val uri = request.requestURI
        val method = request.method
        logger.warn("🔥 isExcluded 체크 중: uri=$uri, method=$method")

        return uri.startsWith("/ws/") || // ✅ WebSocket 예외 처리 추가
                uri.startsWith("/api/users/signup") ||
                uri.startsWith("/api/analytics/**") ||
                uri.startsWith("/api/userAuth/reset-password-request") ||
                uri.startsWith("/api/emailVerification/send-verification") ||
                uri.startsWith("/api/emailVerification/verify-code") ||
                uri.startsWith("/api/emailVerification/check-email") ||
                uri.startsWith("/api/users/check-email") ||
                (uri.matches(Regex("/api/board/\\d+/?")) && method == "GET")||
                uri.startsWith("/api/users/check-nickname") ||
                uri.startsWith("/api/userAuth/reset-password") ||
                uri.startsWith("/api/users/login") ||
                uri.startsWith("/api/auth/kakao-login") ||
                uri.startsWith("/api/users/location") ||
                uri.startsWith("/api/auth/signup") ||
                uri.startsWith("/api/auth/refresh-token") ||
                (uri.startsWith("/api/deals") && method == "GET") ||
                (uri.matches(Regex("/api/barter-bids/deal/\\d+/?")) && method == "GET")

    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val uri = request.requestURI
        logger.warn("🔥 JwtAuthFilter 실행됨: URI=$uri")

        if (isExcluded(request)) {
            logger.warn("✅ 인증 제외 경로로 판단: $uri")
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request)
        if (!token.isNullOrBlank() && token.count { it == '.' } == 2) {
            try {
                val userId = jwtProvider.getUserIdFromToken(token)
                val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
            } catch (e: Exception) {
                logger.warn("Invalid JWT: ${e.message}")
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        val header = request.getHeader("Authorization") ?: return null
        return if (header.startsWith("Bearer ")) header.substring(7) else null
    }
}