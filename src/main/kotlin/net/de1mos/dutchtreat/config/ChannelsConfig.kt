package net.de1mos.dutchtreat.config

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*

class ChannelsConfig(
    private val channelProperties: ChannelProperties,
    private val engine: HttpClientEngine
) {
    suspend fun registerWebhook() {
        val httpClient = HttpClient(engine)
        httpClient.post<HttpResponse>("https://api.telegram.org/bot${channelProperties.telegram.token}/setWebhook") {
            body = MultiPartFormDataContent(formData {
                append("url", channelProperties.telegram.webhook)
            })
        }
    }
}