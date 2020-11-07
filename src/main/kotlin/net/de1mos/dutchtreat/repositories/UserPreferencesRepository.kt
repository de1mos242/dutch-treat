package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.repository.MongoRepository

data class UserPreferences(
        @Id
        val userId: String,
        val lastEventId: String
)

interface UserPreferencesRepository : MongoRepository<UserPreferences, String>