package com.todeal.domain.translate.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.todeal.domain.translate.dto.TranslateRequest
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class TranslationService {
    private val endpoint = "http://localhost:5000/translate"
    private val restTemplate = RestTemplate()
    private val mapper = jacksonObjectMapper()

    fun translate(request: TranslateRequest): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            accept = listOf(MediaType.APPLICATION_JSON)
        }

        // ✅ 1차 번역: source → target
        val translatedText = sendToLibreTranslate(request, headers)

        // ✅ 역번역: target → source
        val backTranslationRequest = TranslateRequest(
            text = translatedText,
            source = request.target,
            target = request.source
        )
        val backTranslatedText = sendToLibreTranslate(backTranslationRequest, headers)

        // ✅ 디버깅용 출력 (추후 비교 분석용)
        println("[번역] 원문: ${request.text}")
        println("[번역] 번역 결과: $translatedText")
        println("[번역] 역번역 결과: $backTranslatedText")

        return translatedText
    }

    private fun sendToLibreTranslate(request: TranslateRequest, headers: HttpHeaders): String {
        val body = mapOf(
            "q" to request.text,
            "source" to request.source,
            "target" to request.target,
            "format" to "text"
        )

        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(endpoint, entity, String::class.java)

        if (response.statusCode != HttpStatus.OK) {
            throw RuntimeException("LibreTranslate 요청 실패: ${response.statusCode}")
        }

        val root = mapper.readTree(response.body)
        return root["translatedText"]?.asText()
            ?: throw RuntimeException("LibreTranslate 응답 파싱 실패")
    }
}
