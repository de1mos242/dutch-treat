package net.de1mos.dutchtreat.config

data class ChannelProperties(val telegram: TelegramChannelConfig) {
    data class TelegramChannelConfig(val token: String, val webhook: String)
}