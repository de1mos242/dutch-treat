package net.de1mos.dutchtreat.config

import com.justai.jaicf.api.BotApi
import net.de1mos.dutchtreat.SetWebhookFailedException
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate


@Configuration
class ChannelsConfig(private val channelProperties: ChannelProperties,
                     private val bot: BotApi,
                     private val restTemplate: RestTemplate): InitializingBean {
    @Bean
    fun telegram(): TelegramChannelCustomImpl {
        return TelegramChannelCustomImpl(bot, channelProperties.telegram.token)
    }

    override fun afterPropertiesSet() {
        if (channelProperties.telegram.webhook.isNotEmpty()) {
            registerWebhook()
        }
    }

    private fun registerWebhook() {
        val headers = HttpHeaders()
        headers.contentType = MediaType.MULTIPART_FORM_DATA
        val body: MultiValueMap<String, Any> = LinkedMultiValueMap()
        body.add("url", channelProperties.telegram.webhook)
        val requestEntity: HttpEntity<MultiValueMap<String, Any>> = HttpEntity(body, headers)
        val serverUrl = "https://api.telegram.org/bot${channelProperties.telegram.token}/setWebhook"

        val response = restTemplate.postForEntity(serverUrl, requestEntity, String::class.java)
        if (response.statusCode != HttpStatus.OK) {
            throw SetWebhookFailedException(response.statusCodeValue, response.body)
        }
    }
}