package net.de1mos.dutchtreat.endpoints

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/telegram")
class ServletApi(private val telegramChannel: TelegramChannelCustomImpl, private val objectMapper: ObjectMapper) {
    private val gson = Gson()

    @PostMapping
    fun telegramWebhook(@RequestBody data: Map<String, Any>) {
        val json = objectMapper.writeValueAsString(data)
        val update = gson.fromJson(json, Update::class.java)
        telegramChannel.process(update)
    }
}