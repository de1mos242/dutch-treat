package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

data class Participant(val id: String, val name: String)
data class Purchase(val id: String, val buyerId: String, val description: String, val amount: BigDecimal, val consumerIds: List<String>? = null)
data class Transfer(val id: String, val senderId: String, val receiverId: String, val amount: BigDecimal)

@Document(collection = "events")
data class Event(
        @Id
        val id: String,
        val name: String,
        val creationTimestamp: LocalDateTime,
        val participants: List<Participant>? = null,
        val purchases: List<Purchase>? = null,
        val transfers: List<Transfer>? = null
)

@Repository
interface EventRepository : MongoRepository<Event, String> {
        @Query(value = "{'id':{\$in: ?0}}", sort = "{'creationTimestamp': 1}")
        fun findAllByIdOrderByCreationTimestampAsc(ids: Iterable<String>): List<Event>
}