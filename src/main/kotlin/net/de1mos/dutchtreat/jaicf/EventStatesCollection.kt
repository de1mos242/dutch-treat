package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.exceptions.EventNotFoundException
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService

class EventStatesCollection(
    private val userPreferencesService: UserPreferencesService,
    private val eventService: EventService
) : BaseStatesCollection(userPreferencesService) {

    fun createEvent(scenario: Scenario) {
        scenario.state("create event") {
            globalActivators { regex("start event (?<val>.+)") }
            action {
                wrapSentry {
                    val eventName = getValFromRegex()
                    val e = eventService.createEvent(eventName)
                    userPreferencesService.updateUserCurrentEvent(context.clientId, e)
                    reactions.say("Great, you created an event '${e.name}'")
                }
            }
        }
    }

    fun getCurrentEvent(scenario: Scenario) {
        scenario.state("get current event") {
            globalActivators { regex("get current event") }
            action {
                wrapSentry {
                    val e = getUserEvent() ?: return@wrapSentry
                    reactions.say("Current event is: ${e.name}")
                }
            }
        }
    }

    fun showEvents(scenario: Scenario) {
        scenario.state("show events") {
            globalActivators { regex("show events") }
            action {
                wrapSentry {
                    val events = userPreferencesService.getUserEvents(context.clientId)
                    if (events.isEmpty()) {
                        reactions.say("There are no events yet, try to start a new one")
                    } else {
                        reactions.say("Your events:\n" + events.mapIndexed { index, event -> "${index + 1}. ${event.name}" }
                            .joinToString("\n"))
                    }
                }
            }
        }
    }

    fun switchToEvent(scenario: Scenario) {
        scenario.state("switch to event") {
            globalActivators { regex("switch to event (?<event>.+)") }
            action {
                wrapSentry {
                    val eventName = getValFromRegex("event")
                    try {
                        userPreferencesService.switchEvent(context.clientId, eventName)
                        reactions.say("Switched to event $eventName")
                    } catch (e: EventNotFoundException) {
                        reactions.say("There is no event ${e.eventName}, but you could start it")
                    }
                }
            }
        }
    }
}