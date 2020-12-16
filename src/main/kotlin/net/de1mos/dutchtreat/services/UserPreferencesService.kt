package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.exceptions.EventNotFoundException
import net.de1mos.dutchtreat.exceptions.UserPreferencesNotFound
import net.de1mos.dutchtreat.repositories.ChannelType
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.UserInfo
import net.de1mos.dutchtreat.repositories.UserPreferences
import net.de1mos.dutchtreat.repositories.UserPreferencesRepository

class UserPreferencesService(
    val userPreferencesRepository: UserPreferencesRepository,
    val eventRepository: EventRepository
) {
    fun updateUserCurrentEvent(userId: String, event: Event) {
        val preferences = userPreferencesRepository.findByIdOrNull(userId) ?: throw UserPreferencesNotFound(userId)
        if (preferences.participatedEventIds.contains(event.id)) {
            userPreferencesRepository.changeEventId(preferences, event.id)
        } else {
            userPreferencesRepository.addEventId(preferences, event.id)
        }
    }

    fun getUserCurrentEvent(userId: String): Event? {
        val preferences = userPreferencesRepository.findByIdOrNull(userId) ?: return null
        if (preferences.lastEventId == null) return null
        return eventRepository.findByIdOrNull(preferences.lastEventId)
    }

    fun getUserEvents(userId: String): List<Event> {
        val preferences = userPreferencesRepository.findByIdOrNull(userId) ?: throw UserPreferencesNotFound(userId)
        return eventRepository.findAllByIdOrderByCreationTimestampAsc(preferences.participatedEventIds)
    }

    fun switchEvent(userId: String, eventName: String) {
        val event = getUserEvents(userId).find {
            it.name.equals(eventName, true)
        } ?: throw EventNotFoundException(eventName)
        updateUserCurrentEvent(userId, event)
    }

    fun createOrUpdateUserInfo(
        userId: String,
        lang: String,
        firstName: String?,
        lastName: String?,
        username: String?,
        channelType: ChannelType
    ) {
        val preferences = userPreferencesRepository.findByIdOrNull(userId)
        val userInfo = UserInfo(firstName, lastName, username, channelType)
        if (preferences == null) {
            val userPreferences = UserPreferences(
                userId = userId,
                lastEventId = null,
                participatedEventIds = emptyList(),
                lang = lang,
                userInfo = userInfo
            )
            userPreferencesRepository.save(userPreferences)
        } else {
            userPreferencesRepository.updateUserInfo(preferences, lang, userInfo)
        }
    }
}