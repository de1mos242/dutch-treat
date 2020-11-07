package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

data class Participant(val name: String)

@Document(collection = "events")
data class Event(
        @Id
        val id: String,
        val name: String,
        val participants: List<Participant>? = null
)

@Repository
interface EventRepository : MongoRepository<Event, String> {
}