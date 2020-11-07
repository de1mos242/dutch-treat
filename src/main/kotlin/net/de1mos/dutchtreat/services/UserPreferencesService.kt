package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.UserPreferences
import net.de1mos.dutchtreat.repositories.UserPreferencesRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class UserPreferencesService(
        val userPreferencesRepository: UserPreferencesRepository,
        val eventRepository: EventRepository
) {

    fun updateUserCurrentEvent(userId: String, event: Event) {
        val preferences = userPreferencesRepository.findByIdOrNull(userId)
        if (preferences != null) {
            userPreferencesRepository.save(preferences.copy(lastEventId = event.id))
        } else {
            userPreferencesRepository.save(UserPreferences(userId, event.id))
        }
    }

    fun getUserCurrentEvent(userId: String): Event? {
        val preferences = userPreferencesRepository.findByIdOrNull(userId) ?: return null
        return eventRepository.findByIdOrNull(preferences.lastEventId)
    }
}