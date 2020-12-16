package net.de1mos.dutchtreat.config

import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import net.de1mos.dutchtreat.exceptions.SetWebhookFailedException

class ChannelsConfig(
    private val channelProperties: ChannelProperties,
    private val engine: HttpClientEngine
) {
    suspend fun registerWebhook() {
        val httpClient = HttpClient(engine)
        val token = channelProperties.telegram.token
        val response = httpClient.post<HttpResponse>("https://api.telegram.org/bot$token/setWebhook") {
            body = MultiPartFormDataContent(formData {
                append("url", channelProperties.telegram.webhook)
            })
        }
        if (response.status != HttpStatusCode.OK) {
            throw SetWebhookFailedException(response.status.value, response.readText())
        }
    }
}