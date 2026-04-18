package ru.bz.barcode.domain.port

import ru.bz.barcode.domain.BarcodeType
import ru.bz.barcode.domain.ImageFormat

interface BarcodeGeneratorPort {
    fun generate(content: String, type: BarcodeType, width: Int, height: Int, format: ImageFormat): ByteArray
}