package ru.bz.barcode.presentation.controller

import com.google.zxing.WriterException
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException
import java.io.UnsupportedEncodingException


@RestControllerAdvice
class GlobalValidationAdvice {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleValidation(ex: ConstraintViolationException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse("Validation error", ex.constraintViolations.joinToString("; ") { it.message }))

    @ExceptionHandler(ResponseStatusException::class)
    fun handleHttpStatus(ex: ResponseStatusException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(ex.statusCode)
            .body(ErrorResponse("Request Error", ex.reason.orEmpty()))

    @ExceptionHandler(UnsupportedEncodingException::class)
    fun handleEncoding(ex: UnsupportedEncodingException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse("Encoding error", ex.message.orEmpty()))

    @ExceptionHandler(WriterException::class)
    fun handleWriter(ex: WriterException): ResponseEntity<ErrorResponse> =
        ResponseEntity.badRequest().body(ErrorResponse("Barcode generation failed", ex.message.orEmpty()))


    data class ErrorResponse(val message: String, val details: String)
}
