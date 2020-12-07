package net.de1mos.dutchtreat.config

import net.de1mos.dutchtreat.endpoints.TelegramHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
class AppRouter(private val telegramHandler: TelegramHandler) {

    @Bean
    fun telegramRoute() = router {
        POST("/telegram", telegramHandler::telegramWebhook)
    }
}