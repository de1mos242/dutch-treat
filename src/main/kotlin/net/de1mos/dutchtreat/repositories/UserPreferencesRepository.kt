package net.de1mos.dutchtreat.repositories

import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.save

data class UserPreferences(
    @BsonId val userId: String,
    val lastEventId: String,
    val participatedEventIds: List<String>
)

interface UserPreferencesRepository {
    fun findByIdOrNull(id: String): UserPreferences?
    fun save(userPreferences: UserPreferences)
}

class UserPreferencesRepositoryImpl(mongo: MongoDatabase) : UserPreferencesRepository {
    private val col = mongo.getCollection<UserPreferences>("userPreferences")
    override fun findByIdOrNull(id: String): UserPreferences? {
        return col.findOne { UserPreferences::userId eq id }
    }

    override fun save(userPreferences: UserPreferences) {
        col.save(userPreferences)
    }

}