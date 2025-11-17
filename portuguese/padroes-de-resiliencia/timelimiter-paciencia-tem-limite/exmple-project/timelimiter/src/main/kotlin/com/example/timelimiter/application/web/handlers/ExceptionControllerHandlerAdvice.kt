package com.example.timelimiter.application.web.handlers

import com.example.timelimiter.application.web.responses.ErrorResponse
import org.apache.coyote.Response
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.util.concurrent.TimeoutException

@ControllerAdvice
class ExceptionControllerHandlerAdvice {

    @ExceptionHandler(TimeoutException::class)
    fun onTimeoutException(ex: TimeoutException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
            .body(
                ErrorResponse(
                    "Timeout Error - There is no response, just an example to handle it",
                    HttpStatus.REQUEST_TIMEOUT.value()
                )
            )
    }
}