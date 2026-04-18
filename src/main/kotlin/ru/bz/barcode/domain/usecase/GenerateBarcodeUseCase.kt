package ru.bz.barcode.domain.usecase

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import ru.bz.barcode.domain.BarcodeType
import ru.bz.barcode.domain.ImageFormat
import ru.bz.barcode.domain.port.BarcodeGeneratorPort

@Component
class GenerateBarcodeUseCase(
    private val generator: BarcodeGeneratorPort
) {
    @Cacheable(
        value = ["barcodes"],
        key = "#content + '_' + #type.name() + '_' + #width + '_' + #height + '_' + #format.name()"
    )
    fun execute(content: String, type: BarcodeType, width: Int, height: Int, format: ImageFormat): ByteArray =
        generator.generate(content, type, width, height, format)

}