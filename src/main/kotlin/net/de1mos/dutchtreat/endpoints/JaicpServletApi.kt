package net.de1mos.dutchtreat.endpoints

import ch.qos.logback.core.LogbackException
import com.justai.jaicf.BotEngine
import com.justai.jaicf.channel.jaicp.JaicpServlet
import com.justai.jaicf.channel.jaicp.JaicpWebhookConnector
import com.justai.jaicf.channel.jaicp.channels.ChatWidgetChannel
import com.justai.jaicf.model.scenario.Scenario
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.apache.logging.slf4j.SLF4JLogger
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.lang.RuntimeException

val bot = object : Scenario() {
    init {
        fallback {
            reactions.say("I don't know what you meat of ${request.input}")
        }
    }
}

val engine = BotEngine(bot.model, activators = emptyArray())

@Component
class JaicpServletApi {

    @Bean
    fun jaicpServlet() {
        val log = Slf4JLoggerFactory.getInstance(this.javaClass)
        log.warn("We are about to start")
        try {
            ServletRegistrationBean(
                    JaicpServlet(
                            JaicpWebhookConnector(
                                    botApi = engine,
                                    accessToken = System.getenv("JAICP_API_TOKEN")
                                            ?: "d38e8351-d1f3-4547-a2d8-b17237eb814a",
                                    channels = listOf(ChatWidgetChannel)
                            )
                    ),
                    "/*"
            ).apply {
                setLoadOnStartup(1)
            }
        } catch (e: RuntimeException) {
            log.error("caught ex ${e.message}")
        }finally {
            log.warn("we are about to end")
        }
    }
}