package net.de1mos.dutchtreat.repositories

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.ensureIndex
import org.litote.kmongo.ensureUniqueIndex
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

data class Invitation(
    @BsonId
    val id: String,
    val code: String,
    val eventId: String,
    val timestamp: LocalDateTime
)

interface InvitationRepository {
    fun findByCode(code: String): Invitation?
    fun save(invitation: Invitation)
    fun deleteById(id: String)
}

class InvitationRepositoryImpl(mongo: MongoDatabase) : InvitationRepository {
    private val col = mongo.getCollection<Invitation>("invitations")

    init {
        col.ensureUniqueIndex(Invitation::code)
        col.ensureIndex(Invitation::timestamp, indexOptions = IndexOptions().expireAfter(3600, TimeUnit.SECONDS))
    }

    override fun findByCode(code: String): Invitation? {
        return col.findOne { Invitation::code eq code }
    }

    override fun save(invitation: Invitation) {
        col.save(invitation)
    }

    override fun deleteById(id: String) {
        col.deleteOne(Invitation::id eq id)
    }
}