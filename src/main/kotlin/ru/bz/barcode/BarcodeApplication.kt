package ru.bz.barcode

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@EnableCaching
@SpringBootApplication
class BarcodeApplication

fun main(args: Array<String>) {
    runApplication<BarcodeApplication>(*args)
}
