package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService

class ParticipantStatesCollection(
    private val eventService: EventService,
    userPreferencesService: UserPreferencesService
) :
    BaseStatesCollection(userPreferencesService) {

    fun addParticipant(scenario: Scenario) {
        scenario.state("add participant") {
            globalActivators { regex("add participant (?<val>.+)") }
            action {
                wrapSentry {
                    val e = getUserEvent() ?: return@wrapSentry
                    val name = getValFromRegex()
                    eventService.addParticipant(e, name)
                    reactions.say("Great, you added $name to your event")
                }
            }
        }
    }

    fun getParticipants(scenario: Scenario) {
        scenario.state("get participants") {
            globalActivators { regex("Get participants") }
            action {
                wrapSentry {
                    val e = getUserEvent() ?: return@wrapSentry
                    val participants = eventService.getParticipants(e)
                    if (participants.isEmpty()) {
                        reactions.say("There are no participants yet, add someone")
                    } else {
                        reactions.say("Participants: ${participants.joinToString(", ")}")
                    }
                }
            }
        }
    }

}