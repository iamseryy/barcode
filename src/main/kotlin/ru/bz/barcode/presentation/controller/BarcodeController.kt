package ru.bz.barcode.presentation.controller

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.bz.barcode.domain.BarcodeType
import ru.bz.barcode.domain.ImageFormat
import ru.bz.barcode.domain.usecase.GenerateBarcodeUseCase
import org.springframework.http.MediaType

@Validated
@RestController
@RequestMapping("/api/barcode")
class BarcodeController(
    private val useCase: GenerateBarcodeUseCase
) {
    @GetMapping("/{type}")
    fun generate(
        @PathVariable type: String,

        @RequestParam
        @NotBlank(message = "Параметр 'content' обязателен и не может состоять только из пробелов")
        @Size(max = 1024, message = "Содержимое не должно превышать 1024 символа")
        content: String,

        @RequestParam(defaultValue = "200")
        @Min(value = 50, message = "Минимальная ширина 50px")
        @Max(value = 2000, message = "Максимальная ширина 2000px")
        width: Int,

        @RequestParam(defaultValue = "200")
        @Min(value = 50, message = "Минимальная высота 50px")
        @Max(value = 2000, message = "Максимальная высота 2000px")
        height: Int,

        @RequestParam(defaultValue = "png")
        @Pattern(regexp = "(?i)png|jpeg", message = "Поддерживаются только форматы: png, jpeg")
        mediatype: String
    ): ResponseEntity<ByteArray> {
        val barcodeType = try {
            BarcodeType.valueOf(type.uppercase().replace("-", "").replace("_", ""))
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Неподдерживаемый тип кода. Доступно: datamatrix, qr, code39, code93, code128, aztec, codabar, pdf417"
            )
        }

        val imageFormat = ImageFormat.valueOf(mediatype.uppercase())
        val responseMediaType = if (imageFormat == ImageFormat.PNG) MediaType.IMAGE_PNG else MediaType.IMAGE_JPEG

        val bytes = useCase.execute(content, barcodeType, width, height, imageFormat)
        return ResponseEntity.ok().contentType(responseMediaType).body(bytes)
    }
}