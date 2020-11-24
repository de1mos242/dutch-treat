package net.de1mos.dutchtreat.endpoints

import com.justai.jaicf.BotEngine
import com.justai.jaicf.channel.jaicp.JaicpServlet
import com.justai.jaicf.channel.jaicp.JaicpWebhookConnector
import com.justai.jaicf.channel.jaicp.channels.ChatWidgetChannel
import com.justai.jaicf.channel.telegram.TelegramChannel
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class JaicpServletApi(val scenario: DutchTreatScenario) {

    @Bean
    fun jaicpServlet(): ServletRegistrationBean<JaicpServlet> {
        return ServletRegistrationBean(
                JaicpServlet(
                        JaicpWebhookConnector(
                                botApi = BotEngine(scenario.bot.model, activators = scenario.activators),
                                accessToken = System.getenv("JAICP_API_TOKEN")
                                        ?: "d38e8351-d1f3-4547-a2d8-b17237eb814a",
                                channels = listOf(ChatWidgetChannel, TelegramChannel)
                        )
                ),
                "/"
        ).apply {
            setLoadOnStartup(1)
        }
    }
}