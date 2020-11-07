package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Participant
import net.de1mos.dutchtreat.repositories.Purchase
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Service
class EventService(val eventRepository: EventRepository) {

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

    fun addPurchase(event: Event, participantName: String, description: String, amount: BigDecimal): PurchaseDto? {
        val p = getParticipant(event, participantName) ?: return null
        val purchase = Purchase(UUID.randomUUID().toString(), p.id, description, amount)
        val newPurchases = event.purchases?.toMutableList() ?: ArrayList()
        newPurchases.add(purchase)
        eventRepository.save(event.copy(purchases = newPurchases))
        return PurchaseDto(participantName, amount, description)
    }

    fun getPurchases(event: Event): List<PurchaseDto> {
        return event.purchases?.map {
            PurchaseDto(
                    event.participants?.find { participant -> participant.id == it.participantId }!!.name,
                    it.amount,
                    it.description)
        } ?: emptyList()
    }

    data class PurchaseDto(val participantName: String, val amount: BigDecimal, val description: String)
}