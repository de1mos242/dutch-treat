package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Document(collection = "events")
data class Event(
        @Id
        val id: String,
        val name: String
)

@Repository
interface EventRepository : MongoRepository<Event, String> {
}