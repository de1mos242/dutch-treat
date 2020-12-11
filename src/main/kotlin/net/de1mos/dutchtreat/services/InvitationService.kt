package net.de1mos.dutchtreat.services

import net.de1mos.dutchtreat.EventNotFoundException
import net.de1mos.dutchtreat.InvitationCodeNotFoundException
import net.de1mos.dutchtreat.repositories.Event
import net.de1mos.dutchtreat.repositories.EventRepository
import net.de1mos.dutchtreat.repositories.Invitation
import net.de1mos.dutchtreat.repositories.InvitationRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class InvitationService(private val invitationRepository: InvitationRepository,
                        private val eventRepository: EventRepository,
                        private val userPreferencesService: UserPreferencesService
) {

    fun invite(event: Event): String {
        val invitation = Invitation(UUID.randomUUID().toString(), UUID.randomUUID().toString(), event.id, LocalDateTime.now(ZoneOffset.UTC))
        invitationRepository.save(invitation)
        return invitation.code
    }

    fun applyInvitation(userId: String, code: String): Event {
        val invitation = invitationRepository.findByCode(code) ?: throw InvitationCodeNotFoundException()

        val event = eventRepository.findByIdOrNull(invitation.eventId)
                ?: throw EventNotFoundException(invitation.eventId)
        userPreferencesService.updateUserCurrentEvent(userId, event)
        invitationRepository.deleteById(invitation.id)
        return event
    }
}