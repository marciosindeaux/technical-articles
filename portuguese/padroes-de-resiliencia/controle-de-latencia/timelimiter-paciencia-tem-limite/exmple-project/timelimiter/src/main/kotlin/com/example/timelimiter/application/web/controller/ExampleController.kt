package com.example.timelimiter.application.web.controller

import com.example.timelimiter.domain.service.ExampleService
import io.github.resilience4j.timelimiter.annotation.TimeLimiter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException

@RequestMapping("/api")
@RestController
class ExampleController (
    private val service: ExampleService
){

    @TimeLimiter(name ="controllerTimeLimiter")
    @GetMapping("data")
    fun getDataFromService() : CompletableFuture<String> = CompletableFuture.supplyAsync {
        service.getDataOrAwaitToTimeout()
    }
}