package com.example.timelimiter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimelimiterApplication

fun main(args: Array<String>) {
	runApplication<TimelimiterApplication>(*args)
}
