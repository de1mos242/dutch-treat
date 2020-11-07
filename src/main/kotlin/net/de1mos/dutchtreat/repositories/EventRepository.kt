package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal

data class Participant(val id: String, val name: String)
data class Purchase(val id: String, val participantId: String, val description: String, val amount: BigDecimal)

@Document(collection = "events")
data class Event(
        @Id
        val id: String,
        val name: String,
        val participants: List<Participant>? = null,
        val purchases: List<Purchase>? = null
)

@Repository
interface EventRepository : MongoRepository<Event, String> {
}