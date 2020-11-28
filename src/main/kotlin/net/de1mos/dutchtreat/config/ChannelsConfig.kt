package net.de1mos.dutchtreat.config

import com.justai.jaicf.api.BotApi
import com.justai.jaicf.channel.telegram.TelegramChannel
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ChannelsConfig(private val channelProperties: ChannelProperties,
                     private val bot: BotApi): InitializingBean {

    @Bean
    fun telegram() : TelegramChannel {
        return TelegramChannel(bot, channelProperties.telegram.token)
    }

    override fun afterPropertiesSet() {
        telegram().run()
    }
}