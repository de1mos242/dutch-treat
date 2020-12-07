package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.EventNotFoundException
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.UserPreferences
import net.de1mos.dutchtreat.repositories.UserPreferencesRepository
import org.springframework.stereotype.Service

@Service
class UserPreferencesService(
        val userPreferencesRepository: UserPreferencesRepository,
        val eventRepository: EventRepository
) {
    fun updateUserCurrentEvent(userId: String, event: Event) {
        val preferences = userPreferencesRepository.findById(userId).block()
        if (preferences != null) {
            val newEvents = if (!preferences.participatedEventIds.contains(event.id)) {
                preferences.participatedEventIds.toMutableList().also { it.add(event.id) }
            } else {
                preferences.participatedEventIds
            }
            userPreferencesRepository.save(preferences.copy(lastEventId = event.id, participatedEventIds = newEvents)).block()
        } else {
            userPreferencesRepository.save(UserPreferences(userId, event.id, listOf(event.id))).block()
        }
    }

    fun getUserCurrentEvent(userId: String): Event? {
        val preferences = userPreferencesRepository.findById(userId).block() ?: return null
        return eventRepository.findById(preferences.lastEventId).block()
    }

    fun getUserEvents(userId: String): List<Event> {
        val preferences = userPreferencesRepository.findById(userId).block() ?: return emptyList()
        return eventRepository.findAllByIdOrderByCreationTimestampAsc(preferences.participatedEventIds).collectList().block()!!
    }

    fun switchEvent(userId: String, eventName: String) {
        val event = getUserEvents(userId).find { it.name.equals(eventName, true) } ?: throw EventNotFoundException(eventName)
        updateUserCurrentEvent(userId, event)
    }
}