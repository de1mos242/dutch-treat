package net.de1mos.dutchtreat

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import net.de1mos.dutchtreat.endpoints.TelegramHandler

class TelegramRouter(private val telegramHandler: TelegramHandler) {
    fun getRoute(route: Route): Route {
        return route.post("/telegram") {
            telegramHandler.telegramWebhook(call.receiveText())
            call.respondText("Ok", ContentType.Text.Plain)
        }
    }
}