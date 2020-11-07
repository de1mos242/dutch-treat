package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.repositories.EventRepository
import org.springframework.stereotype.Service

@Service
class DutchTreatScenario(val eventRepository: EventRepository) {
    final val bot: Scenario

    init {
        bot = object : Scenario() {
            init {
                fallback {
                    reactions.say("I don't know what you mean of ${request.input} v2")
                }
            }
        }
    }

}