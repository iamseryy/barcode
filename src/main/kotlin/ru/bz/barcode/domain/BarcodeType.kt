package ru.bz.barcode.domain

enum class BarcodeType {
    DATAMATRIX,
    QR,
    CODE39,
    CODE93,
    CODE128,
    AZTEC,
    CODABAR,
    PDF417;


    companion object {
        fun fromHttpValue(value: String): BarcodeType? = when (value.lowercase()) {
            "datamatrix" -> DATAMATRIX
            "qr" -> QR
            "code39" -> CODE39
            "code93" -> CODE93
            "code128" -> CODE128
            "aztec" -> AZTEC
            "codabar" -> CODABAR
            "pdf417" -> PDF417
            else -> null
        }
    }
}





