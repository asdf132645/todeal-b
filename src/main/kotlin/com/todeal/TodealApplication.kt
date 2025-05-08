package com.todeal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication  // ❌ exclude 제거됨
class TodealApplication

fun main(args: Array<String>) {
    runApplication<TodealApplication>(*args)
}
