package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario

class DutchTreatScenario(
    private val systemStatesCollection: SystemStatesCollection,
    private val eventStatesCollection: EventStatesCollection,
    private val invitationStatesCollection: InvitationStatesCollection,
    private val participantStatesCollection: ParticipantStatesCollection,
    private val purchaseStatesCollection: PurchaseStatesCollection,
    private val transferStatesCollection: TransferStatesCollection,
    private val balanceStatesCollection: BalanceStatesCollection
) {
    fun getBot(): Scenario {
        return bot
    }

    private val bot = object : Scenario() {
        init {
            systemStatesCollection.start(this)
            systemStatesCollection.version(this)
            systemStatesCollection.help(this)
            systemStatesCollection.fullHelp(this)
            systemStatesCollection.fallback(this)

            eventStatesCollection.createEvent(this)
            eventStatesCollection.getCurrentEvent(this)
            eventStatesCollection.showEvents(this)
            eventStatesCollection.switchToEvent(this)

            invitationStatesCollection.inviteUser(this)
            invitationStatesCollection.activateInvitationCode(this)

            participantStatesCollection.addParticipant(this)
            participantStatesCollection.getParticipants(this)

            purchaseStatesCollection.addPurchase(this)
            purchaseStatesCollection.removePurchase(this)
            purchaseStatesCollection.addConsumer(this)
            purchaseStatesCollection.getPurhcases(this)

            transferStatesCollection.addTransfer(this)
            transferStatesCollection.removeTransfer(this)
            transferStatesCollection.getTransfers(this)

            balanceStatesCollection.getBalance(this)
        }
    }
}