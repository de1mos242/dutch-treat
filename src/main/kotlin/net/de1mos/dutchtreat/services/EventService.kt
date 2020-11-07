package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.ParticipantNotFound
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Participant
import net.de1mos.dutchtreat.repositories.Purchase
import net.de1mos.dutchtreat.repositories.Transfer
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Service
class EventService(val eventRepository: EventRepository) {
    data class PurchaseDto(val buyerName: String, val amount: BigDecimal, val description: String)
    data class TransferDto(val senderName: String, val receiverName: String, val amount: BigDecimal)

    fun createEvent(eventName: String): Event {
        val event = Event(UUID.randomUUID().toString(), eventName)
        eventRepository.save(event)
        return event
    }

    fun addParticipant(event: Event, participantName: String) {
        if (getParticipant(event, participantName) != null) {
            return
        }
        val newParticipants = event.participants?.toMutableList() ?: ArrayList()
        newParticipants.add(Participant(UUID.randomUUID().toString(), participantName))
        val newEvent = event.copy(participants = newParticipants)
        eventRepository.save(newEvent)
    }

    fun getParticipants(event: Event): List<String> {
        return event.participants?.map { it.name } ?: emptyList()
    }

    fun getParticipant(event: Event, participantName: String): Participant? {
        if (event.participants == null) {
            return null
        }
        return event.participants.find { it.name.equals(participantName, true) }
    }

    fun addPurchase(event: Event, participantName: String, description: String, amount: BigDecimal): PurchaseDto {
        val p = getParticipant(event, participantName) ?: throw ParticipantNotFound(participantName)
        val purchase = Purchase(UUID.randomUUID().toString(), p.id, description, amount)
        val newPurchases = event.purchases?.toMutableList() ?: ArrayList()
        newPurchases.add(purchase)
        eventRepository.save(event.copy(purchases = newPurchases))
        return PurchaseDto(participantName, amount, description)
    }

    fun getPurchases(event: Event): List<PurchaseDto> {
        return event.purchases?.map {
            PurchaseDto(
                    event.participants?.find { participant -> participant.id == it.buyerId }!!.name,
                    it.amount,
                    it.description)
        } ?: emptyList()
    }

    fun addTransfer(event: Event, senderName: String, receiverName: String, amount: BigDecimal): TransferDto {
        val sender = getParticipant(event, senderName) ?: throw ParticipantNotFound(senderName)
        val receiver = getParticipant(event, receiverName) ?: throw ParticipantNotFound(receiverName)
        val newTransfers = event.transfers?.toMutableList() ?: ArrayList()
        val transfer = Transfer(UUID.randomUUID().toString(), sender.id, receiver.id, amount)
        newTransfers.add(transfer)
        eventRepository.save(event.copy(transfers = newTransfers))
        return TransferDto(sender.name, receiver.name, amount)
    }

    fun getTransfers(event: Event): List<TransferDto> {
        return event.transfers?.map {
            TransferDto(
                    event.participants?.find { participant -> participant.id == it.senderId }!!.name,
                    event.participants.find { participant -> participant.id == it.receiverId }!!.name,
                    it.amount
            )
        } ?: emptyList()
    }
}