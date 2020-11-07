package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Participant
import org.springframework.stereotype.Service
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
        if (event.participants != null && event.participants.any { it.name == participantName }) {
            return
        }
        val newParticipants = event.participants?.toMutableList() ?: ArrayList()
        newParticipants.add(Participant(participantName))
        val newEvent = event.copy(participants = newParticipants)
        eventRepository.save(newEvent)
    }

    fun getParticipants(event: Event): List<String> {
        return event.participants?.map { it.name } ?: emptyList()
    }
}