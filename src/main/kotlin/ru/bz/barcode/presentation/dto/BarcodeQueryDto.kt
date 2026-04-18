package ru.bz.barcode.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springdoc.core.annotations.ParameterObject


@ParameterObject
@Schema(description = "Barcode generation parameters ")
data class BarcodeQueryDto(
    @field:Schema(description = "Text to be encoded", example = "https://example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @field:NotBlank(message = "{validation.barcode.content.notblank}")
    @field:Size(max = 1024, message = "{validation.barcode.content.size}")
    val content: String,

    @field:Schema(description = "Image width (px)", example = "250")
    @field:Min(value = 50, message = "{validation.barcode.dimensions.min}")
    @field:Max(value = 2000, message = "{validation.barcode.dimensions.max}")
    val width: Int = 200,

    @field:Schema(description = "Image height (px)", example = "250")
    @field:Min(value = 50, message = "{validation.barcode.dimensions.min}")
    @field:Max(value = 2000, message = "{validation.barcode.dimensions.max}")
    val height: Int = 200,

    @field:Schema(description = "Image format", example = "png", allowableValues = ["png", "jpeg"])
    @field:Pattern(regexp = "(?i)png|jpeg", message = "{validation.barcode.format.pattern}")
    val mediatype: String = "png"
)