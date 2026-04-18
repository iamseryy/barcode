package ru.bz.barcode.presentation.controller

import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException


@RestControllerAdvice
class GlobalValidationAdvice {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Ошибка валидации", ex.constraintViolations.joinToString("; ") { it.message }))

    @ExceptionHandler(ResponseStatusException::class)
    fun handleHttpStatus(ex: ResponseStatusException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(ex.statusCode)
            .body(ErrorResponse("Ошибка запроса", ex.reason.orEmpty()))

    data class ErrorResponse(val message: String, val details: String)
}
