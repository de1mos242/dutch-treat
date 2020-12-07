package net.de1mos.dutchtreat.repositories

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Document(collection = "invitations")
data class Invitation(
    @Id
    val id: String,
    @Indexed
    val code: String,
    val eventId: String,
    @Indexed(expireAfterSeconds = 3600)
    val timestamp: LocalDateTime
)

interface InvitationRepository : ReactiveMongoRepository<Invitation, String> {
    fun findByCode(code: String): Mono<Invitation>
}