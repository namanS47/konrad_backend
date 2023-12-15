package com.example.konrad

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KonradApplication

fun main(args: Array<String>) {
	runApplication<KonradApplication>(*args)
}
