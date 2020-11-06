package net.de1mos.dutchtreat.endpoints

import com.justai.jaicf.BotEngine
import com.justai.jaicf.channel.jaicp.JaicpServlet
import com.justai.jaicf.channel.jaicp.JaicpWebhookConnector
import com.justai.jaicf.channel.jaicp.channels.ChatWidgetChannel
import com.justai.jaicf.model.scenario.Scenario
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

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
    fun jaicpServlet() = ServletRegistrationBean(
            JaicpServlet(
                    JaicpWebhookConnector(
                            botApi = engine,
                            accessToken = System.getenv("JAICP_API_TOKEN") ?: "d38e8351-d1f3-4547-a2d8-b17237eb814a",
                            channels = listOf(ChatWidgetChannel)
                    )
            ),
            "/jaicf/*"
    ).apply {
        setLoadOnStartup(1)
    }
}