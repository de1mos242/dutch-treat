package net.de1mos.dutchtreat

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import net.de1mos.dutchtreat.config.appModule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

class DutchTreatApplication: KoinComponent {
	val telegramRoute by inject<TelegramRouter>()

	fun runServer() {
		val server = embeddedServer(Netty, port = 8080) {
			routing {
				telegramRoute.getRoute(this)

				get("/") {
					call.respondText("Hello World!", ContentType.Text.Plain)
				}
				get("/demo") {
					call.respondText("HELLO WORLD!")
				}
			}
		}
		server.start(wait = true)
	}
}

fun main() {
	startKoin {
		printLogger()
		modules(appModule)
	}

	DutchTreatApplication().runServer()
}
