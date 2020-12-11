package net.de1mos.dutchtreat.config

import com.justai.jaicf.api.BotApi
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class ChannelsConfig(
    @Value("\${channels.telegram.token}") private val token: String,
    @Value("\${channels.telegram.webhook}") private val webhook: String,
    private val bot: BotApi
): InitializingBean {
    @Bean
    fun telegram(): TelegramChannelCustomImpl {
        return TelegramChannelCustomImpl(bot, token)
    }

    override fun afterPropertiesSet() {
        if (webhook.isNotEmpty()) {
            registerWebhook()
        }
    }

    private fun registerWebhook() {
        val body: MultiValueMap<String, String> = LinkedMultiValueMap()
        body.add("url", webhook)

        val request = WebClient.create("https://api.telegram.org")
            .post()
            .uri("/bot${token}/setWebhook")
            .body(BodyInserters.fromFormData(body))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
        request.exchange().block()?.bodyToMono(String::class.java)?.block()
    }
}