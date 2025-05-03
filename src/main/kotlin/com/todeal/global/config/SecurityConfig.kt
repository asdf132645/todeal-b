package com.todeal.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/jobs/today").permitAll()  // ✅ 이 URL만 허용
                it.anyRequest().authenticated()                // 나머지는 인증 필요
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}
