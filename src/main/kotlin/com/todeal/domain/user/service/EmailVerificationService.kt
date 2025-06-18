package com.todeal.domain.user.service

import com.todeal.domain.user.infra.EmailCodeStore
import jakarta.mail.Message
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailVerificationService(
    private val mailSender: JavaMailSender,
    private val emailCodeStore: EmailCodeStore
) {
    fun sendVerificationCode(email: String) {
        val code = (100000..999999).random().toString()
        emailCodeStore.saveCode(email, code)

        val message: MimeMessage = mailSender.createMimeMessage()
        message.setFrom(InternetAddress("amber132623@gmail.com"))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email))
        message.subject = "[ToDEAL] 이메일 인증코드"
        message.setText("ToDEAL 가입을 위한 인증코드는 [$code] 입니다. 5분 내에 입력해주세요.")
        mailSender.send(message)
    }

    fun verifyCode(email: String, inputCode: String) {
        val storedCode = emailCodeStore.getCode(email)
            ?: throw IllegalArgumentException("인증코드가 존재하지 않습니다.")
        if (storedCode != inputCode) {
            throw IllegalArgumentException("인증코드가 일치하지 않습니다.")
        }
        emailCodeStore.removeCode(email)
    }
}
