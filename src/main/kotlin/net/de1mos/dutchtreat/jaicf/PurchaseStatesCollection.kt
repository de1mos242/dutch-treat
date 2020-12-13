package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.exceptions.NoPurchasesException
import net.de1mos.dutchtreat.exceptions.ParticipantNotFoundException
import net.de1mos.dutchtreat.exceptions.PurchaseNotFoundException
import net.de1mos.dutchtreat.services.EventService
import net.de1mos.dutchtreat.services.UserPreferencesService

class PurchaseStatesCollection(
    private val eventService: EventService,
    userPreferencesService: UserPreferencesService
) :
    BaseStatesCollection(userPreferencesService) {

    fun addPurchase(scenario: Scenario) {
        scenario.state("add purchase") {
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
    }

    fun removePurchase(scenario: Scenario) {
        scenario.state("remove purchase") {
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
    }

    fun addConsumer(scenario: Scenario) {
        scenario.state("add consumer") {
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
    }

    fun getPurhcases(scenario: Scenario) {
        scenario.state("get purchases")
        {
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
    }
}