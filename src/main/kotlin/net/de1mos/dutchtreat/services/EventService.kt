package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.exceptions.NoPurchasesException
import net.de1mos.dutchtreat.exceptions.ParticipantNotFoundException
import net.de1mos.dutchtreat.exceptions.PurchaseNotFoundException
import net.de1mos.dutchtreat.exceptions.TransferNotFoundException
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Participant
import net.de1mos.dutchtreat.repositories.Purchase
import net.de1mos.dutchtreat.repositories.Transfer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import kotlin.collections.ArrayList

class EventService(val eventRepository: EventRepository) {
    data class PurchaseDto(
        val buyerName: String,
        val amount: BigDecimal,
        val description: String,
        val consumers: List<String>
    )

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
        val participant = Participant(UUID.randomUUID().toString(), participantName)
        eventRepository.addParticipant(event, participant)
    }

    fun getParticipants(event: Event): List<String> {
        return event.participants?.map { it.name } ?: emptyList()
    }

    private fun getParticipant(event: Event, participantName: String): Participant? {
        if (event.participants == null) {
            return null
        }
        return event.participants.find { it.name.equals(participantName, true) }
    }

    fun addPurchase(event: Event, buyerName: String, description: String, amount: BigDecimal): PurchaseDto {
        val p = getParticipant(event, buyerName) ?: throw ParticipantNotFoundException(buyerName)
        val purchase = Purchase(UUID.randomUUID().toString(), p.id, description, amount)
        eventRepository.addPurchase(event, purchase)
        return PurchaseDto(buyerName, amount, description, emptyList())
    }

    fun removePurchase(event: Event, purchaseNumber: Int): PurchaseDto {
        val purchase = findPurchase(event, purchaseNumber - 1)
        eventRepository.removePurchase(event, purchase)
        return toDto(event, purchase, event.participants)
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
        val purchase = findPurchase(event, purchaseIndex)
        if (purchase.consumerIds != null && purchase.consumerIds.contains(p.id)) {
            return toDto(event, purchase, event.participants)
        }
        val newConsumers = purchase.consumerIds?.toMutableList() ?: ArrayList()
        newConsumers.add(p.id)
        val newPurchase = purchase.copy(consumerIds = newConsumers)
        val newPurchases = event.purchases!!.toMutableList()
        newPurchases[purchaseIndex] = newPurchase
        eventRepository.save(event.copy(purchases = newPurchases))
        return toDto(event, purchase, event.participants)
    }

    private fun findPurchase(event: Event, purchaseIndex: Int): Purchase {
        return try {
            event.purchases?.get(purchaseIndex) ?: throw PurchaseNotFoundException(purchaseIndex + 1)
        } catch (e: IndexOutOfBoundsException) {
            throw PurchaseNotFoundException(purchaseIndex + 1)
        }
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
        val transfer = Transfer(UUID.randomUUID().toString(), sender.id, receiver.id, amount)
        eventRepository.addTransfer(event, transfer)
        return TransferDto(sender.name, receiver.name, amount)
    }

    fun removeTransfer(event: Event, positionNumber: Int): TransferDto {
        val transfer = findTransfer(event, positionNumber - 1)
        eventRepository.removeTransfer(event, transfer)
        return transferDto(event, transfer)
    }

    fun getTransfers(event: Event): List<TransferDto> {
        return event.transfers?.map { transferDto(event, it) } ?: emptyList()
    }

    private fun transferDto(event: Event, it: Transfer): TransferDto {
        return TransferDto(
            event.participants?.find { participant -> participant.id == it.senderId }!!.name,
            event.participants.find { participant -> participant.id == it.receiverId }!!.name,
            it.amount
        )
    }

    private fun findTransfer(event: Event, transferIndex: Int): Transfer {
        return try {
            event.transfers?.get(transferIndex) ?: throw TransferNotFoundException(transferIndex + 1)
        } catch (e: IndexOutOfBoundsException) {
            throw TransferNotFoundException(transferIndex + 1)
        }
    }


}