package com.todeal.domain.translate.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.todeal.domain.translate.dto.TranslateRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Service
class TranslationService(
    @Value("\${papago.client-id}") private val clientId: String,
    @Value("\${papago.client-secret}") private val clientSecret: String
) {
    private val endpoint = "https://papago.apigw.ntruss.com/nmt/v1/translation"
    private val restTemplate = RestTemplate()
    private val mapper = jacksonObjectMapper()

    fun translate(request: TranslateRequest): String {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
            accept = listOf(MediaType.APPLICATION_JSON)
            set("X-NCP-APIGW-API-KEY-ID", clientId)
            set("X-NCP-APIGW-API-KEY", clientSecret)
        }

        val formData: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
            add("source", request.source)
            add("target", request.target)
            add("text", request.text)
        }

        val entity = HttpEntity(formData, headers)
        val response = restTemplate.postForEntity(endpoint, entity, String::class.java)

        if (response.statusCode != HttpStatus.OK) {
            throw RuntimeException("Papago 번역 실패: ${response.statusCode}")
        }

        val body = response.body ?: throw RuntimeException("Papago 응답 없음")

        // ✅ JSON 파싱해서 translatedText 추출
        val root = mapper.readTree(body)
        val translatedText = root["message"]["result"]["translatedText"]?.asText()
            ?: throw RuntimeException("Papago 응답 파싱 실패")

        return translatedText
    }
}
