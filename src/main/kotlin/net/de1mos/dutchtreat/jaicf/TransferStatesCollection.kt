package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.exceptions.ParticipantNotFoundException
import net.de1mos.dutchtreat.exceptions.TransferNotFoundException
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService

class TransferStatesCollection(
    private val eventService: EventService,
    userPreferencesService: UserPreferencesService
) :
    BaseStatesCollection(userPreferencesService) {

    fun addTransfer(scenario: Scenario) {
        scenario.state("add transfer") {
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
    }

    fun removeTransfer(scenario: Scenario) {
        scenario.state("remove transfer") {
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
    }

    fun getTransfers(scenario: Scenario) {
        scenario.state("get transfers") {
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
    }

    private fun EventService.TransferDto.toPrettyString(): String {
        return "${this.senderName} gave ${this.receiverName} ${this.amount.toPrettyString()}"
    }
}