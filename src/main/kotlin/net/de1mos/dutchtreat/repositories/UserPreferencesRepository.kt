package net.de1mos.dutchtreat.repositories

import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.combine
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.push
import org.litote.kmongo.save
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.litote.kmongo.setValue

data class UserPreferences(
    @BsonId val userId: String,
    val lastEventId: String?,
    val participatedEventIds: List<String>,
    val lang: String?,
    val userInfo: UserInfo?
)

data class UserInfo(
    val firstName: String?,
    val lastName: String?,
    val username: String?,
    val channel: ChannelType
)

enum class ChannelType {
    TELEGRAM,
    UNKNOWN
}

interface UserPreferencesRepository {
    fun findByIdOrNull(id: String): UserPreferences?
    fun save(userPreferences: UserPreferences)
    fun changeEventId(userPreferences: UserPreferences, eventId: String)
    fun addEventId(userPreferences: UserPreferences, eventId: String)
    fun updateUserInfo(userPreferences: UserPreferences, lang: String, userInfo: UserInfo)
}

class UserPreferencesRepositoryImpl(mongo: MongoDatabase) : UserPreferencesRepository {
    private val col = mongo.getCollection<UserPreferences>("userPreferences")
    override fun findByIdOrNull(id: String): UserPreferences? {
        return col.findOne { UserPreferences::userId eq id }
    }

    override fun save(userPreferences: UserPreferences) {
        col.save(userPreferences)
    }

    override fun changeEventId(userPreferences: UserPreferences, eventId: String) {
        col.updateOne(
            UserPreferences::userId eq userPreferences.userId,
            set(UserPreferences::lastEventId setTo eventId)
        )
    }

    override fun addEventId(userPreferences: UserPreferences, eventId: String) {
        col.updateOne(
            UserPreferences::userId eq userPreferences.userId,
            combine(
                setValue(UserPreferences::lastEventId, eventId),
                push(UserPreferences::participatedEventIds, eventId)
            )
        )
    }

    override fun updateUserInfo(userPreferences: UserPreferences, lang: String, userInfo: UserInfo) {
        col.updateOne(
            UserPreferences::userId eq userPreferences.userId,
            set(UserPreferences::lang setTo lang, UserPreferences::userInfo setTo userInfo)
        )
    }

}