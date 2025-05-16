package com.todeal.domain.user.service

import com.todeal.domain.user.repository.UserRepository
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*
import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage

@Service
class PasswordResetService(
    private val userRepository: UserRepository,
    private val tokenService: PasswordResetTokenService,
    private val mailSender: JavaMailSender,
    private val passwordEncoder: PasswordEncoder
) {

    fun sendResetEmail(email: String) {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("존재하지 않는 이메일입니다.")

        val token = UUID.randomUUID().toString()
        tokenService.saveToken(token, user.id)

        val resetUrl = "http://localhost:3000/reset-password?token=$token"

        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            message.setFrom(InternetAddress("amber132623@gmail.com"))
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
            message.subject = "[ToDEAL] 비밀번호 재설정 안내"
            message.setContent(
                """
                <p>안녕하세요, ToDEAL입니다.</p>
                <p>아래 버튼을 클릭하여 비밀번호를 재설정해주세요.</p>
                <p><a href="$resetUrl" style="background:#2A2E9D;color:#fff;padding:10px 20px;text-decoration:none;">비밀번호 재설정</a></p>
                <p>해당 링크는 30분 동안만 유효합니다.</p>
                """.trimIndent(),
                "text/html; charset=UTF-8"
            )
            mailSender.send(message)
        } catch (e: Exception) {
            println("📧 메일 전송 실패: ${e.message}")
        }
    }

    fun resetPassword(token: String, newPassword: String) {
        val userId = tokenService.getUserIdByToken(token)
            ?: throw IllegalArgumentException("유효하지 않은 토큰입니다.")

        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }

        user.password = passwordEncoder.encode(newPassword)
        userRepository.save(user)
        tokenService.deleteToken(token)
    }
}
