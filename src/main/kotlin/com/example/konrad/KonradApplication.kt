package com.example.konrad

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
@EnableCaching
class KonradApplication

fun main(args: Array<String>) {
	runApplication<KonradApplication>(*args)
}
