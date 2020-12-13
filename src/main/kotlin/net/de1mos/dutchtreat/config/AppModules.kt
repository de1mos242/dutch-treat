package net.de1mos.dutchtreat.config

import com.justai.jaicf.BotEngine
import com.justai.jaicf.api.BotApi
import io.ktor.client.engine.cio.*
import io.ktor.util.*
import net.de1mos.dutchtreat.TelegramRouter
import net.de1mos.dutchtreat.channels.TelegramChannelCustomImpl
import net.de1mos.dutchtreat.endpoints.TelegramHandler
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.EventRepositoryImpl
import net.de1mos.dutchtreat.repositories.InvitationRepository
import net.de1mos.dutchtreat.repositories.InvitationRepositoryImpl
import net.de1mos.dutchtreat.repositories.UserPreferencesRepository
import net.de1mos.dutchtreat.repositories.UserPreferencesRepositoryImpl
import net.de1mos.dutchtreat.services.BalanceService
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.InvitationService
import net.de1mos.dutchtreat.services.UserPreferencesService
import org.koin.dsl.module
import org.litote.kmongo.KMongo

@KtorExperimentalAPI
val appModule = module {
    single { TelegramRouter(get()) }
    single { TelegramHandler(get()) }
    single { TelegramChannelCustomImpl(get(), getProperty("telegram_token")) }
    single { DutchTreatScenario(get(), get(), get(), get(), get()) }
    single<BotApi> {
        BotEngine(
            get(DutchTreatScenario::class).getBot().model,
            activators = get(ActivatorsConfig::class).getActivators()
        )
    }
    single { ActivatorsConfig(get()) }
    single { DialogflowConfig(getProperty("dialogflow_base64")) }
    single {
        ChannelsConfig(
            ChannelProperties(
                ChannelProperties.TelegramChannelConfig(
                    getProperty("telegram_token"),
                    getProperty("telegram_webhook")
                )
            ), get()
        )
    }
    single { SentryConfig(getProperty("sentry_dsn"), getProperty("sentry_environment")) }
    single { AppInfoConfig(getProperty("app_version"))}

    single { EventService(get()) }
    single<EventRepository> { EventRepositoryImpl(get()) }

    single { UserPreferencesService(get(), get()) }
    single<UserPreferencesRepository> { UserPreferencesRepositoryImpl(get()) }

    single { InvitationService(get(), get(), get()) }
    single<InvitationRepository> { InvitationRepositoryImpl(get()) }

    single { BalanceService() }

    single { KMongo.createClient(getProperty("mongodb_url")).getDatabase(getProperty("mongodb_database")) }
    single { CIO.create() }
}