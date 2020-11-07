package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(val eventRepository: EventRepository) {

    fun createEvent(eventName: String) : Event {
        val event = Event(UUID.randomUUID().toString(), eventName)
        eventRepository.save(event)
        return event
    }
}