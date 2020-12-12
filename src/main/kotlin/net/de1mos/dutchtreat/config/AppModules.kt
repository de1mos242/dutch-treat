package net.de1mos.dutchtreat.config

import com.justai.jaicf.BotEngine
import net.de1mos.dutchtreat.TelegramRouter
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import net.de1mos.dutchtreat.endpoints.TelegramHandler
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.koin.dsl.module

val appModule = module {
    single { TelegramRouter(get()) }
    single { TelegramHandler(get()) }
    single { TelegramChannelCustomImpl(get(), getProperty("telegram_token")) }
    single { DutchTreatScenario(get(), get(), get(), get(), get()) }
    single { BotEngine(get(DutchTreatScenario::class).getBot().model, activators = get(ActivatorsConfig::class).getActivators()) }
}