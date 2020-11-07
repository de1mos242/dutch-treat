package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.context.ActionContext
import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class DutchTreatScenario(
        val eventService: EventService,
        val userPreferencesService: UserPreferencesService
) {
    final val bot: Scenario

    init {
        bot = object : Scenario() {
            init {
                state("start") {
                    globalActivators {
                        event("/start")
                        regex("/start")
                    }
                    action {
                        reactions.say("""
                            Hello, this is a Dutch Treat bot!
                            I'm here to help you with party events.
                            You can start with creating event, just say "start event %event_name%.
                            Next you can add some participants to this event, just say "add participant %name%. 
                        """.trimIndent())
                    }
                }

                state("create event") {
                    globalActivators {
                        regex("start event (?<val>.+)")
                    }

                    action {
                        val eventName = getValFromRegex()
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
                        val e = getUserEvent() ?: return@action
                        reactions.say("Current event is: ${e.name}")
                    }
                }

                state("add participant") {
                    globalActivators {
                        regex("add participant (?<val>.+)")
                    }
                    action {
                        val e = getUserEvent() ?: return@action
                        val name = getValFromRegex()
                        eventService.addParticipant(e, name)
                        reactions.say("Great, you added $name to your event")
                    }
                }

                state("get participants") {
                    globalActivators { regex("Get participants") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val participants = eventService.getParticipants(e)
                        if (participants.isEmpty()) {
                            reactions.say("There are no participants yet, add someone")
                        } else {
                            reactions.say("Participants: ${participants.joinToString(", ")}")
                        }
                    }
                }

                state("add purchase") {
                    globalActivators { regex("(?<participant>.+) bought (?<desc>.+) for (?<cost>[\\d\\.]+).*") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val participantName = getValFromRegex("participant")
                        val desciption = getValFromRegex("desc")
                        val costStr = getValFromRegex("cost")
                        val cost = try {
                            BigDecimal(costStr)
                        } catch (e: NumberFormatException) {
                            reactions.say("Can't reacognize $costStr as amount of money")
                            return@action
                        }

                        val p = eventService.addPurchase(e, participantName, desciption, cost)
                        if (p != null) {
                            reactions.say("Great, added $participantName purchase for ${p.amountString}")
                        } else {
                            reactions.say("There is no participant with name $participantName, add him or her before")
                        }
                    }
                }

                state("get purchases") {
                    globalActivators { regex("Get purchases") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val purchases = eventService.getPurchases(e)
                        if (purchases.isEmpty()) {
                            reactions.say("There are no purchases yet, add a new one")
                        } else {
                            reactions.say("Purchases list:\n" + purchases.mapIndexed { index, purchase ->
                                "${index + 1}. ${purchase.participantName} bought ${purchase.description} for ${purchase.amountString}"
                            }.joinToString("\n"))
                        }
                    }
                }

                fallback {
                    reactions.say("I don't know what you mean with ${request.input}")
                }
            }
        }
    }

    private fun ActionContext.getValFromRegex(group: String = "val") = activator.regex?.group(group)!!.trim()
    private fun ActionContext.getUserEvent(): Event? {
        val e = userPreferencesService.getUserCurrentEvent(context.clientId)
        if (e == null) {
            reactions.say("There is no current event, but you can create a new one")
            return null
        }
        return e
    }
}