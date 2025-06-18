package com.todeal.domain.translate.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

        val body = mapOf(
            "q" to request.text,
            "source" to request.source,
            "target" to request.target,
            "format" to "text"
        )

        val entity = HttpEntity(body, headers)
        val response = restTemplate.postForEntity(endpoint, entity, String::class.java)

        if (response.statusCode != HttpStatus.OK) {
            throw RuntimeException("LibreTranslate 번역 실패: ${response.statusCode}")
        }

        val root = mapper.readTree(response.body)
        val translatedText = root["translatedText"]?.asText()
            ?: throw RuntimeException("LibreTranslate 응답 파싱 실패")

        return translatedText
    }
}
