package net.de1mos.dutchtreat.endpoints

import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

@Component
class TelegramHandler(private val telegramChannel: TelegramChannelCustomImpl) {
    private val gson = Gson()

    fun telegramWebhook(request: ServerRequest): Mono<ServerResponse> {
        return ServerResponse.ok().body(
            request.bodyToMono(String::class.java)
                .map { gson.fromJson(it, Update::class.java) }
                .map { telegramChannel.process(it) }, String::class.java
        )

    }
}