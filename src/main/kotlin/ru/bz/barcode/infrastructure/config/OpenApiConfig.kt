package ru.bz.barcode.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration



@Configuration
class OpenApiConfig {

    @Value("\${server.port:8080}")
    private val serverPort: String = "8080"

    @Bean
    fun customOpenAPI(): OpenAPI {
        val serverUrl = "http://localhost:$serverPort"

        return OpenAPI()
            .servers(listOf(Server().url(serverUrl).description("Local server")))
            .info(
                Info()
                    .title("Barcode Generator API")
                    .version("1.0.0")
                    .description("Генерация штрихкодов: DataMatrix, QR, Code 39, Code 93, Code 128, Aztec Code, Codabar, PDF417, \n\n" +
                            "### Использование:\n" +
                            "1. Выберите тип кода в ссылке: `datamatrix`, `qr`, `code39`, `code93`, `code128`, `aztec`, `codabar`, `pdf417`\n" +
                            "2. Укажите `content` — текст для кодирования\n" +
                            "3. Настройте размер и формат вывода: `png`, `jpeg`")
                    .contact(
                        Contact()
                            .name("Baltic Shipyard Development Team")
                            .email("inforln@bz.com")
                            .url("https://portal2.bz.ru")
                    )
                    .license(
                        License()
                            .name("MIT")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
    }
}