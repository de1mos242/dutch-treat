package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService
import org.springframework.stereotype.Service

@Service
class DutchTreatScenario(
        val eventService: EventService,
        val userPreferencesService: UserPreferencesService
) {
    final val bot: Scenario

    init {
        bot = object : Scenario() {
            init {
                state("create event") {
                    globalActivators {
                        regex("start event (?<val>.*)")
                    }

                    action {
                        val eventName = activator.regex?.group("val")!!.trim()
                        val e = eventService.createEvent(eventName)
                        userPreferencesService.updateUserCurrentEvent(context.clientId, e)
                        reactions.say("Great, you created an event '${e.name}'")
                    }
                }

                state("get current event") {
                    globalActivators {
                        regex("get current event")
                    }
                    action {
                        val e = userPreferencesService.getUserCurrentEvent(context.clientId)
                        if (e != null) {
                            reactions.say("Current event is: ${e.name}")
                        } else {
                            reactions.say("There is no current event, but you can create a new one")
                        }
                    }
                }

                state("add participant") {

                }

                fallback {
                    reactions.say("I don't know what you mean with ${request.input}")
                }
            }
        }
    }

}