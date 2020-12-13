package net.de1mos.dutchtreat

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import io.sentry.Sentry
import kotlinx.coroutines.runBlocking
import net.de1mos.dutchtreat.config.AppInfoConfig
import net.de1mos.dutchtreat.config.ChannelsConfig
import net.de1mos.dutchtreat.config.SentryConfig
import net.de1mos.dutchtreat.config.appModule
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

@KoinApiExtension
class DutchTreatApplication : KoinComponent {
    private val telegramRoute by inject<TelegramRouter>()
    private val channelsConfig by inject<ChannelsConfig>()
    private val sentryConfig by inject<SentryConfig>()
    private val appInfoConfig by inject<AppInfoConfig>()

    fun runServer() {
        Sentry.init {
            it.dsn = sentryConfig.dsn
            it.environment = sentryConfig.env
            it.serverName = getComputerName()
            it.release = appInfoConfig.version
        }
        try {
            runBlocking {
                channelsConfig.registerWebhook()
            }
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
        } catch (e: Exception) {
            Sentry.captureException(e)
            throw e
        }
    }
}

object Dev {
    @KtorExperimentalAPI
    @KoinApiExtension
    @JvmStatic
    fun main(args: Array<String>) {
        startKoin("/koin-local.properties")
        runServer()
    }
}

@KtorExperimentalAPI
@KoinApiExtension
fun main(args: Array<String>) {
    startKoin(if (args.isNotEmpty()) args[0] else "/koin.properties")
    runServer()
}

@KoinApiExtension
private fun runServer() {
    DutchTreatApplication().runServer()
}

@KtorExperimentalAPI
private fun startKoin(props: String) {
    startKoin {
        printLogger()
        fileProperties(props)
        environmentProperties()
        modules(appModule)
    }
}

private fun getComputerName(): String? {
    val env = System.getenv()
    return if (env.containsKey("COMPUTERNAME")) env["COMPUTERNAME"] else if (env.containsKey("HOSTNAME")) env["HOSTNAME"] else "Unknown Computer"
}
