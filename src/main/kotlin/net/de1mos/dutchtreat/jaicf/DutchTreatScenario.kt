package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.activator.regex.regex
import com.justai.jaicf.context.ActionContext
import com.justai.jaicf.model.scenario.Scenario
import io.sentry.Sentry
import net.de1mos.dutchtreat.config.AppInfoConfig
import net.de1mos.dutchtreat.exceptions.EventNotFoundException
import net.de1mos.dutchtreat.exceptions.InvitationCodeNotFoundException
import net.de1mos.dutchtreat.exceptions.NoPurchasesException
import net.de1mos.dutchtreat.exceptions.ParticipantNotFoundException
import net.de1mos.dutchtreat.exceptions.PurchaseNotFoundException
import net.de1mos.dutchtreat.exceptions.TransferNotFoundException
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.services.BalanceService
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.InvitationService
import net.de1mos.dutchtreat.services.UserPreferencesService
import java.math.BigDecimal
import java.text.DecimalFormat

class DutchTreatScenario(
    private val eventService: EventService,
    private val userPreferencesService: UserPreferencesService,
    private val balanceService: BalanceService,
    private val invitationService: InvitationService,
    private val appInfoConfig: AppInfoConfig
) {
    fun getBot(): Scenario {
        return bot
    }

    private val bot = object : Scenario() {
        init {
            state("start") {
                globalActivators {
                    event("/start")
                    regex("/start")
                }
                action {
                    wrapSentry {
                        reactions.say(
                            """
                            Hello, this is a Dutch Treat bot!
                            I'm here to help you with party events.
                            You can start with creating event just say "start event %event_name%".
                            To see the list of available commands just send "help".
                        """.trimIndent()
                        )
                    }
                }
            }

            state("version") {
                globalActivators { regex("version") }
                action { wrapSentry { reactions.say(appInfoConfig.version) } }
            }

            state("help") {
                globalActivators {
                    regex("help")
                    intent("help")
                }
                action {
                    wrapSentry {
                        reactions.say(
                            """
                        You can send me these common commands:
                        start event %Event name%
                        add participant %Participant name%
                        %Participant name% bought %Purchase description% for %Purchase cost% - to add purchase
                        %Participant name% gave %Participant name% %Transfer amount% - to transfer money between participants
                        get balance - show current event balance between participants
                        full help - to see all available commands
                    """.trimIndent()
                        )
                    }
                }
            }

            state("full help") {
                globalActivators {
                    regex("full help")
                    intent("full help")
                }
                action {
                    wrapSentry {
                        reactions.say(
                            """
                        You can send me these commands:
                        -- Events
                        start event %Event name%
                        get current event
                        show events - see all events you're participated in
                        switch to event %Event name%
                        
                        -- Collaboration
                        invite user - generate invitation code, that you can send other user to add him to your event
                        activate %Code% - activate invitation code, so user will get full access to event and switch to it
                        
                        -- Participants
                        add participant %Participant name%
                        get participants - show all current participants
                        
                        -- Purchases
                        %Participant name% bought %Purchase description% for %Purchase cost% - to add purchase
                        Get purchases - show all purchases with their positions
                        Add %Participant name% as a consumer to purchase %Position number% - add concrete consumers to a purchase. Without it all participants consider as consumers
                        Add %Participant name% as a consumer to the last purchase - add concrete consumers to the last purchase. Without it all participants consider as consumers
                        Remove purchase %Position number% - remove purchase at position
                        
                        -- Transfers
                        %Participant name% gave %Participant name% %Transfer amount% - to transfer money between participants
                        Get transfers - show all transfers
                        Remove transfer %Position number% - remove transfer at position
                        
                        -- Balance
                        get balance - show current event balance between participants
                    """.trimIndent()
                        )
                    }
                }
            }

            state("create event") {
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

            state("get current event") {
                globalActivators { regex("get current event") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        reactions.say("Current event is: ${e.name}")
                    }
                }
            }

            state("show events") {
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

            state("switch to event") {
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

            state("invite user") {
                globalActivators { regex("invite user") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val code = invitationService.invite(e)
                        reactions.say("Send this code to user: $code")
                    }
                }
            }

            state("activate invitation code") {
                globalActivators { regex("activate (?<val>[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}).*") }
                action {
                    wrapSentry {
                        val code = getValFromRegex()
                        try {
                            val event = invitationService.applyInvitation(context.clientId, code)
                            reactions.say("You were successfully added to event ${event.name}")
                        } catch (e: InvitationCodeNotFoundException) {
                            reactions.say("Invitation code not found")
                        } catch (e: EventNotFoundException) {
                            reactions.say("event is gone...")
                        }
                    }
                }
            }

            state("add participant") {
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

            state("get participants") {
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

            state("add purchase") {
                globalActivators { regex("(?<buyer>.+) bought (?<desc>.+) for (?<cost>[\\d\\.]+).*") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val participantName = getValFromRegex("buyer")
                        val description = getValFromRegex("desc")
                        val cost = toBigDecimal(getValFromRegex("cost")) ?: return@wrapSentry

                        try {
                            val p = eventService.addPurchase(e, participantName, description, cost)
                            reactions.say("Great, added $participantName purchase for ${p.amount.toPrettyString()}")
                        } catch (e: ParticipantNotFoundException) {
                            reactions.say("There is no participant with name ${e.name}, add him or her before")
                        }
                    }
                }
            }

            state("remove purchase") {
                globalActivators { regex("Remove purchase (?<position>[\\d]+).*") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val position = getValFromRegex("position").toInt()
                        try {
                            val p = eventService.removePurchase(e, position)
                            reactions.say("Purchase ${p.description} removed")
                        } catch (e: PurchaseNotFoundException) {
                            reactions.say("Purchase with position ${e.position} not found")
                        }
                    }
                }
            }

            state("add consumer") {
                globalActivators {
                    regex("Add (?<consumer>.+) as a consumer to purchase (?<position>[\\d]+).*")
                    regex("Add (?<consumer>.+) as a consumer to the last purchase(?<position>\\-?1?)")
                }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val consumerName = getValFromRegex("consumer")
                        val positinString = getSafeValFromRegex("position")
                        val position = (if (positinString.isNullOrEmpty()) "-1" else positinString).toInt()

                        try {
                            val p = eventService.addConsumerToPurchase(e, consumerName, position)
                            reactions.say("Great, I've added $consumerName as a consumer for ${p.description}")
                        } catch (e: ParticipantNotFoundException) {
                            reactions.say("There is no participant with name ${e.name}, add him or her before")
                        } catch (e: NoPurchasesException) {
                            reactions.say("There are no purchases yet, add a new one")
                        } catch (e: PurchaseNotFoundException) {
                            reactions.say("Purchase with position ${e.position} not found")
                        }
                    }
                }
            }

            state("get purchases") {
                globalActivators { regex("Get purchases") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val purchases = eventService.getPurchases(e)
                        if (purchases.isEmpty()) {
                            reactions.say("There are no purchases yet, add a new one")
                        } else {
                            reactions.say("Purchases list:\n" + purchases.mapIndexed { index, purchase ->
                                val consumers = if (purchase.consumers.isEmpty()) "" else {
                                    " for " + purchase.consumers.reduceIndexed { consumerIdx, acc, s ->
                                        acc + if (consumerIdx < purchase.consumers.size - 1) {
                                            ", "
                                        } else {
                                            " and "
                                        } + s
                                    }
                                }
                                "${index + 1}. ${purchase.buyerName} bought ${purchase.description} for ${purchase.amount.toPrettyString()}" + consumers
                            }.joinToString("\n"))
                        }
                    }
                }
            }

            state("add transfer") {
                globalActivators { regex("(?<sender>.+) gave (?<receiver>.+) (?<cost>[\\d\\.]+).*") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val senderName = getValFromRegex("sender")
                        val receiverName = getValFromRegex("receiver")
                        val cost = toBigDecimal(getValFromRegex("cost")) ?: return@wrapSentry

                        try {
                            val t = eventService.addTransfer(e, senderName, receiverName, cost)
                            reactions.say("Great, sent ${t.amount.toPrettyString()} from ${t.senderName} to ${t.receiverName}")
                        } catch (e: ParticipantNotFoundException) {
                            reactions.say("There is no participant with name ${e.name}, add him or her before")
                        }
                    }
                }
            }

            state("remove transfer") {
                globalActivators { regex("Remove transfer (?<position>[\\d]+).*") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val position = getValFromRegex("position").toInt()
                        try {
                            val t = eventService.removeTransfer(e, position)
                            reactions.say("Transfer '${t.toPrettyString()}' removed")
                        } catch (e: TransferNotFoundException) {
                            reactions.say("Transfer with position ${e.position} not found")
                        }
                    }
                }
            }

            state("get transfers") {
                globalActivators { regex("Get transfers") }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val transfers = eventService.getTransfers(e)
                        if (transfers.isEmpty()) {
                            reactions.say("There are no transfers yet, add a new one")
                        } else {
                            reactions.say("Transfers list:\n" + transfers.mapIndexed { index, transfer ->
                                "${index + 1}. ${transfer.toPrettyString()}"
                            }.joinToString("\n"))
                        }
                    }
                }
            }

            state("get balance") {
                globalActivators {
                    regex("Get balance")
                    intent("Get balance")
                }
                action {
                    wrapSentry {
                        val e = getUserEvent() ?: return@wrapSentry
                        val balance = balanceService.calculateBalance(e)
                        reactions.say("Current balance\n" + balance.joinToString("\n") {
                            when (it.balance.compareTo(BigDecimal.ZERO)) {
                                -1 -> "${it.participantName} should give ${it.balance.abs().toPrettyString()}"
                                0 -> "${it.participantName} owes nobody and nobody owes him or her"
                                1 -> "${it.participantName} should get ${it.balance.toPrettyString()}"
                                else -> throw IllegalStateException(it.balance.toPrettyString())
                            }
                        })
                    }
                }
            }

            fallback {
                reactions.say("I don't know what you mean with ${request.input}, send 'help' to see available commands")
            }
        }
    }

    private fun ActionContext.getValFromRegex(group: String = "val") = activator.regex?.group(group)!!.trim()
    private fun ActionContext.getSafeValFromRegex(group: String = "val") =
        activator.regex?.matcher?.group(group)?.trim()

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
        return DecimalFormat("0.00").format(this).replace(".", ",")
    }

    private fun EventService.TransferDto.toPrettyString(): String {
        return "${this.senderName} gave ${this.receiverName} ${this.amount.toPrettyString()}"
    }

    private fun wrapSentry(block: () -> Unit) {
        try {
            block()
        } catch (e: Exception) {
            Sentry.captureException(e, "Catch in scenario")
        }
    }
}