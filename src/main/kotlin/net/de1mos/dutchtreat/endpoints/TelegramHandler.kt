package net.de1mos.dutchtreat.endpoints

import com.github.kotlintelegrambot.entities.Update
import com.google.gson.Gson
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl

class TelegramHandler(private val telegramChannel: TelegramChannelCustomImpl) {
    private val gson = Gson()

    fun telegramWebhook(requestText: String) {
        val update = gson.fromJson(requestText, Update::class.java)
        telegramChannel.process(update)
    }
}