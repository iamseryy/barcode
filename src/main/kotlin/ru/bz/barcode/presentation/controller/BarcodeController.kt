package ru.bz.barcode.presentation.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.ExampleObject
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
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
    @Operation(
        summary = "Generate barcode",
        description = "Supports: DataMatrix, QR, Code 39, Code 93, Code 128, Aztec Code, Codabar, PDF417"
    )
    @ApiResponses(value = [
        ApiResponse(
            responseCode = "200",
            content = [
            Content(
                mediaType = "image/png",
                schema = Schema(type = "string", format = "binary")
            ),
            Content(
                mediaType = "image/jpeg",
                schema = Schema(type = "string", format = "binary")
            )
        ]),
        ApiResponse(responseCode = "400", description = "Invalid parameters") ,
        ApiResponse(responseCode = "500", description = "Internal generation error")
    ])
    fun generate(
        @Parameter(
            name = "type",
            description = "The barcode type",
            required = true,
            `in` = ParameterIn.PATH,
            schema = Schema(
                type = "string",
                allowableValues = ["datamatrix", "qr", "code39", "code93", "code128", "aztec", "codabar", "pdf417"],
            ),
            examples = [ExampleObject(name = "DataMatrix", value = "datamatrix", summary = "DataMatrix")]
        )
        @PathVariable type: String,

        @RequestParam
        @NotBlank(message = "The 'content' parameter is required and cannot consist of spaces only")
        @Size(max = 1024, message = "The content must not exceed 1024 characters")
        content: String,

        @RequestParam(defaultValue = "200")
        @Min(value = 50, message = "Minimum width 50px")
        @Max(value = 2000, message = "Maximum height 2000px")
        width: Int,

        @RequestParam(defaultValue = "200")
        @Min(value = 50, message = "Minimum width 50px")
        @Max(value = 2000, message = "Maximum height 2000px")
        height: Int,

        @RequestParam(defaultValue = "png")
        @Pattern(regexp = "(?i)png|jpeg", message = "Supported formats only: png, jpeg")
        mediatype: String
    ): ResponseEntity<ByteArray> {
        /*
        val barcodeType = try {
            BarcodeType.valueOf(type.uppercase().replace("-", "").replace("_", ""))
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Неподдерживаемый тип кода. Доступно: datamatrix, qr, code39, code93, code128, aztec, codabar, pdf417"
            )
        }
        */
        val barcodeType = BarcodeType.fromHttpValue(type)
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Unsupported code type. Available: datamatrix, qr, code39, code93, code128, aztec, codabar, pdf417"
            )


        val imageFormat = ImageFormat.valueOf(mediatype.uppercase())
        val responseMediaType = if (imageFormat == ImageFormat.PNG) MediaType.IMAGE_PNG else MediaType.IMAGE_JPEG

        val bytes = useCase.execute(content, barcodeType, width, height, imageFormat)
        return ResponseEntity.ok().contentType(responseMediaType).body(bytes)
    }
}