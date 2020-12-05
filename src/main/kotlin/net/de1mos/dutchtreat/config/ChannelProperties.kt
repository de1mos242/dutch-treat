package net.de1mos.dutchtreat.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "channels")
data class ChannelProperties(val telegram: TelegramChannelConfig) {
    data class TelegramChannelConfig(val token: String, val webhook: String)
}