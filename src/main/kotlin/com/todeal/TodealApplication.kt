package com.todeal.todeal

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodealApplication

fun main(args: Array<String>) {
    runApplication<TodealApplication>(*args)
}
