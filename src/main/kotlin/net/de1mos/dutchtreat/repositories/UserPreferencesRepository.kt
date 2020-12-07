package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

data class UserPreferences(
        @Id
        val userId: String,
        val lastEventId: String,
        val participatedEventIds: List<String>
)

interface UserPreferencesRepository : ReactiveMongoRepository<UserPreferences, String>