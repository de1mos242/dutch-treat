package net.de1mos.dutchtreat.repositories

import com.mongodb.client.MongoDatabase
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.`in`
import org.litote.kmongo.ascending
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import java.math.BigDecimal
import java.time.LocalDateTime

data class Participant(@BsonId val id: String, val name: String)
data class Purchase(
    @BsonId val id: String,
    val buyerId: String,
    val description: String,
    val amount: BigDecimal,
    val consumerIds: List<String>? = null
)

data class Transfer(@BsonId val id: String, val senderId: String, val receiverId: String, val amount: BigDecimal)

data class Event(
    @BsonId val id: String,
    val name: String,
    val creationTimestamp: LocalDateTime,
    val participants: List<Participant>? = null,
    val purchases: List<Purchase>? = null,
    val transfers: List<Transfer>? = null
)

interface EventRepository {
    fun findAllByIdOrderByCreationTimestampAsc(ids: Iterable<String>): List<Event>
    fun findByIdOrNull(id: String): Event?
    fun save(event: Event)
}

class EventRepositoryImpl(mongo: MongoDatabase) : EventRepository {
    private val col = mongo.getCollection<Event>("events")

    override fun findAllByIdOrderByCreationTimestampAsc(ids: Iterable<String>): List<Event> {
        return col.find(Event::id `in` ids).sort(ascending(Event::creationTimestamp)).toList()
    }

    override fun findByIdOrNull(id: String): Event? {
        return col.findOne { Event::id eq id }
    }

    override fun save(event: Event) {
        col.save(event)
    }
}