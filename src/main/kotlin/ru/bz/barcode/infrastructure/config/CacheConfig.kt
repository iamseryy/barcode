package ru.bz.barcode.infrastructure.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration


@Configuration
class CacheConfig {
    @Bean
    fun cacheManager(): CacheManager {
        val caffeineCache = CaffeineCache(
            "barcodes",
            Caffeine.newBuilder()
                .maximumSize(1_000)
                .expireAfterAccess(Duration.ofMinutes(30))
                .recordStats()
                .build()
        )

        return SimpleCacheManager().apply { setCaches(listOf(caffeineCache)) }
    }
}