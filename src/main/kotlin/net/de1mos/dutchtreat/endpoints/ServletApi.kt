package net.de1mos.dutchtreat.endpoints

import com.justai.jaicf.channel.http.HttpBotChannelServlet
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import org.springframework.boot.web.servlet.ServletRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component

@Component
class ServletApi(private val telegramChannel: TelegramChannelCustomImpl) {

    @Bean
    fun botServlet(): ServletRegistrationBean<HttpBotChannelServlet> {
        return ServletRegistrationBean(
                HttpBotChannelServlet(
                        telegramChannel
                ),
                "/telegram"
        )
    }
}