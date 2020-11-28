package net.de1mos.dutchtreat.config

import com.justai.jaicf.BotEngine
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class BotConfig(private val scenario: DutchTreatScenario) {

    @Bean
    fun bot(): BotEngine {
        return BotEngine(scenario.bot.model, activators = scenario.activators)
    }
}