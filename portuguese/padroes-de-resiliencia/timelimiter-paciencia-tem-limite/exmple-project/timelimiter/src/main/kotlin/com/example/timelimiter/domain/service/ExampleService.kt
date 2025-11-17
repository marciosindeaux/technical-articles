package com.example.timelimiter.domain.service

import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextInt

@Service
class ExampleService {


    fun getDataOrAwaitToTimeout(): String {
        val generateNumber = Random.nextInt(0, 21)
        return if(generateNumber % 2 == 0) {
            return "Pair number ? Lucky!!"
        } else {
            TimeUnit.MILLISECONDS.sleep(((generateNumber+1) * 1000).toLong())
            "Maybe not today hun"
        }
    }
}