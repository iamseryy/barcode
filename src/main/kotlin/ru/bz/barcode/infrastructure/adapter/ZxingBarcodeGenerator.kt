package ru.bz.barcode.infrastructure.adapter

import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import org.springframework.stereotype.Service
import ru.bz.barcode.domain.BarcodeType
import ru.bz.barcode.domain.ImageFormat
import ru.bz.barcode.domain.port.BarcodeGeneratorPort
import java.io.ByteArrayOutputStream
import java.util.EnumMap
import javax.imageio.ImageIO


/*
@Service
class ZxingBarcodeGenerator : BarcodeGeneratorPort {

    override fun generate(
        content: String,
        type: BarcodeType,
        width: Int,
        height: Int,
        format: ImageFormat
    ): ByteArray {
        // Маппинг типов кодов
        val zxFormat = when (type) {
            BarcodeType.DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            BarcodeType.QR_CODE -> BarcodeFormat.QR_CODE
            BarcodeType.CODE_128 -> BarcodeFormat.CODE_128
        }

        // 🔑 Базовые подсказки для всех типов
        val hints = mutableMapOf<EncodeHintType, Any>(
            EncodeHintType.CHARACTER_SET to "UTF-8"  // ✅ Критично для кириллицы
        )

        // Специфичные настройки для DataMatrix
        if (type == BarcodeType.DATA_MATRIX) {
            hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_SQUARE
            // ✅ Для кириллицы: принудительно используем Base256-режим через UTF-8 байты
            // Это обходит ограничение ISO-8859-1 в HighLevelEncoder
        }

        // Для QR Code можно добавить коррекцию ошибок
        if (type == BarcodeType.QR_CODE) {
            hints[EncodeHintType.ERROR_CORRECTION] = "L" // L, M, Q, H
        }

        try {
            // ✅ Кодируем: MultiFormatWriter сам обработает UTF-8 при наличии подсказки
            val bitMatrix = MultiFormatWriter().encode(content, zxFormat, width, height, hints)
            val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)

            return ByteArrayOutputStream().use { baos ->
                ImageIO.write(bufferedImage, format.name.lowercase(), baos)
                baos.toByteArray()
            }
        } catch (e: IllegalArgumentException) {
            // 🎯 Перехватываем ошибку кодировки и даём понятное сообщение
            if (e.message?.contains("ISO-8859-1") == true) {
                throw IllegalArgumentException(
                    "Содержимое содержит символы, не поддерживаемые в текущей кодировке. " +
                            "Для кириллицы в DataMatrix используйте короткие строки или альтернативно QR Code.",
                    e
                )
            }
            throw e
        }
    }
}
*/

@Service
class ZxingBarcodeGenerator : BarcodeGeneratorPort {

    override fun generate(
        content: String,
        type: BarcodeType,
        width: Int,
        height: Int,
        format: ImageFormat
    ): ByteArray {
        val zxFormat = when (type) {
            BarcodeType.DATAMATRIX -> BarcodeFormat.DATA_MATRIX
            BarcodeType.QR -> BarcodeFormat.QR_CODE
            BarcodeType.CODE39 -> BarcodeFormat.CODE_39
            BarcodeType.CODE93 -> BarcodeFormat.CODE_93
            BarcodeType.CODE128 -> BarcodeFormat.CODE_128
            BarcodeType.AZTEC -> BarcodeFormat.AZTEC
            BarcodeType.CODABAR -> BarcodeFormat.CODABAR
            BarcodeType.PDF417 -> BarcodeFormat.PDF_417
        }

        // Базовые подсказки
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
        }

        // Специфичные подсказки
        if (type == BarcodeType.DATAMATRIX) {
            hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_SQUARE
        } else if (type == BarcodeType.QR) {
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.M
        }

        // Для DataMatrix: запаковать UTF-8 байты в ISO-8859-1 строку (обход 3.5.4)
        val toEncode: String = if (type == BarcodeType.DATAMATRIX) {
            val bytes = content.toByteArray(Charsets.UTF_8)
            String(bytes, Charsets.ISO_8859_1)
        } else {
            content
        }

        try {
            val bitMatrix = MultiFormatWriter().encode(toEncode, zxFormat, width, height, hints)
            val bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix)

            return ByteArrayOutputStream().use { baos ->
                ImageIO.write(bufferedImage, format.name.lowercase(), baos)
                baos.toByteArray()
            }
        } catch (e: WriterException) {
            throw IllegalArgumentException(
                "Не удалось закодировать содержимое в $type. Возможно строка слишком длинная для выбранного размера или неподдерживаемые символы.",
                e
            )
        } catch (e: IllegalArgumentException) {
            // перенаправляем, оставляя оригинал
            throw e
        }
    }
}
