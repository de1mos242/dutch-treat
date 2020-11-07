package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.NoPurchasesException
import net.de1mos.dutchtreat.ParticipantNotFoundException
import net.de1mos.dutchtreat.PurchaseNotFoundException
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Participant
import net.de1mos.dutchtreat.repositories.Purchase
import net.de1mos.dutchtreat.repositories.Transfer
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

@Service
class EventService(val eventRepository: EventRepository) {
    data class PurchaseDto(val buyerName: String, val amount: BigDecimal, val description: String, val consumers: List<String>)
    data class TransferDto(val senderName: String, val receiverName: String, val amount: BigDecimal)

    fun createEvent(eventName: String): Event {
        val event = Event(UUID.randomUUID().toString(), eventName, LocalDateTime.now(ZoneOffset.UTC))
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

    fun addPurchase(event: Event, buyerName: String, description: String, amount: BigDecimal): PurchaseDto {
        val p = getParticipant(event, buyerName) ?: throw ParticipantNotFoundException(buyerName)
        val purchase = Purchase(UUID.randomUUID().toString(), p.id, description, amount)
        val newPurchases = event.purchases?.toMutableList() ?: ArrayList()
        newPurchases.add(purchase)
        eventRepository.save(event.copy(purchases = newPurchases))
        return PurchaseDto(buyerName, amount, description, emptyList())
    }

    fun addConsumerToPurchase(event: Event, consumerName: String, purchaseNumber: Int = -1): PurchaseDto {
        val p = getParticipant(event, consumerName) ?: throw ParticipantNotFoundException(consumerName)
        var purchaseIndex = purchaseNumber - 1
        if (purchaseNumber == -1) {
            if (event.purchases == null || event.purchases.isEmpty()) {
                throw NoPurchasesException()
            }
            purchaseIndex = event.purchases.size - 1
        }
        val purchase = try {
            event.purchases?.get(purchaseIndex) ?: throw PurchaseNotFoundException(purchaseNumber)
        } catch (e:IndexOutOfBoundsException) {
            throw PurchaseNotFoundException(purchaseNumber)
        }
        if (purchase.consumerIds != null && purchase.consumerIds.contains(p.id)) {
            return toDto(event, purchase, event.participants)
        }
        val newConsumers = purchase.consumerIds?.toMutableList() ?: ArrayList()
        newConsumers.add(p.id)
        val newPurchase = purchase.copy(consumerIds = newConsumers)
        val newPurchases = event.purchases.toMutableList()
        newPurchases[purchaseIndex] = newPurchase
        eventRepository.save(event.copy(purchases = newPurchases))
        return toDto(event, purchase, event.participants)
    }

    fun getPurchases(event: Event): List<PurchaseDto> {
        return event.purchases?.map {
            toDto(event, it, event.participants)
        } ?: emptyList()
    }

    private fun toDto(event: Event, it: Purchase, participants: List<Participant>?): PurchaseDto {
        return PurchaseDto(
                event.participants?.find { p -> p.id == it.buyerId }!!.name,
                it.amount,
                it.description,
                it.consumerIds?.map { c -> participants?.find { p -> p.id == c }!!.name } ?: emptyList())
    }

    fun addTransfer(event: Event, senderName: String, receiverName: String, amount: BigDecimal): TransferDto {
        val sender = getParticipant(event, senderName) ?: throw ParticipantNotFoundException(senderName)
        val receiver = getParticipant(event, receiverName) ?: throw ParticipantNotFoundException(receiverName)
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