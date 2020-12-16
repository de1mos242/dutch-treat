package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.services.BalanceService
import net.de1mos.dutchtreat.services.UserPreferencesService
import java.math.BigDecimal

class BalanceStatesCollection(
    private val balanceService: BalanceService,
    userPreferencesService: UserPreferencesService
) :
    BaseStatesCollection(userPreferencesService) {

    fun getBalance(scenario: Scenario) {
        scenario.state("get balance") {
            globalActivators {
                regex("Get balance")
                intent("Get balance")
            }
            action {
                wrapAction(this) {
                    val e = getUserEvent() ?: return@wrapAction
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
    }
}