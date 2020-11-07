package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.context.ActionContext
import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.ParticipantNotFound
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.services.BalanceService
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.text.DecimalFormat

@Service
class DutchTreatScenario(
        val eventService: EventService,
        val userPreferencesService: UserPreferencesService,
        val balanceService: BalanceService
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
                    globalActivators { regex("start event (?<val>.+)") }
                    action {
                        val eventName = getValFromRegex()
                        val e = eventService.createEvent(eventName)
                        userPreferencesService.updateUserCurrentEvent(context.clientId, e)
                        reactions.say("Great, you created an event '${e.name}'")
                    }
                }


                state("get current event") {
                    globalActivators { regex("get current event") }
                    action {
                        val e = getUserEvent() ?: return@action
                        reactions.say("Current event is: ${e.name}")
                    }
                }

                state("add participant") {
                    globalActivators { regex("add participant (?<val>.+)") }
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
                    globalActivators { regex("(?<buyer>.+) bought (?<desc>.+) for (?<cost>[\\d\\.]+).*") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val participantName = getValFromRegex("buyer")
                        val description = getValFromRegex("desc")
                        val cost = toBigDecimal(getValFromRegex("cost")) ?: return@action

                        try {
                            val p = eventService.addPurchase(e, participantName, description, cost)
                            reactions.say("Great, added $participantName purchase for ${p.amount.toPrettyString()}")
                        } catch (e: ParticipantNotFound) {
                            reactions.say("There is no participant with name ${e.name}, add him or her before")
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
                                "${index + 1}. ${purchase.buyerName} bought ${purchase.description} for ${purchase.amount.toPrettyString()}"
                            }.joinToString("\n"))
                        }
                    }
                }

                state("add transfer") {
                    globalActivators { regex("(?<sender>.+) gave (?<receiver>.+) (?<cost>[\\d\\.]+).*") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val senderName = getValFromRegex("sender")
                        val receiverName = getValFromRegex("receiver")
                        val cost = toBigDecimal(getValFromRegex("cost")) ?: return@action

                        try {
                            val t = eventService.addTransfer(e, senderName, receiverName, cost)
                            reactions.say("Great, sent ${t.amount.toPrettyString()} from ${t.senderName} to ${t.receiverName}")
                        } catch (e: ParticipantNotFound) {
                            reactions.say("There is no participant with name ${e.name}, add him or her before")
                        }
                    }
                }

                state("get transfers") {
                    globalActivators { regex("Get transfers") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val transfers = eventService.getTransfers(e)
                        if (transfers.isEmpty()) {
                            reactions.say("There are no transfers yet, add a new one")
                        } else {
                            reactions.say("Transfers list:\n" + transfers.mapIndexed { index, purchase ->
                                "${index + 1}. ${purchase.senderName} gave ${purchase.receiverName} ${purchase.amount.toPrettyString()}"
                            }.joinToString("\n"))
                        }
                    }
                }

                state("get balance") {
                    globalActivators { regex("Get balance") }
                    action {
                        val e = getUserEvent() ?: return@action
                        val balance = balanceService.calculateBalance(e)
                        reactions.say("Current balance\n" + balance.map {
                            when (it.balance.compareTo(BigDecimal.ZERO)) {
                                -1 -> "${it.participantName} should give ${it.balance.abs().toPrettyString()}"
                                0 -> "${it.participantName} owes nobody and nobody owes him or her"
                                1 -> "${it.participantName} should get ${it.balance.toPrettyString()}"
                                else -> throw IllegalStateException(it.balance.toPrettyString())
                            }
                        }.joinToString("\n"))
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

    private fun ActionContext.toBigDecimal(str: String): BigDecimal? {
        return try {
            BigDecimal(str)
        } catch (e: NumberFormatException) {
            reactions.say("Can't recognize $str as amount of money")
            null
        }
    }

    private fun BigDecimal.toPrettyString(): String {
        return DecimalFormat("0.00").format(this)
    }
}