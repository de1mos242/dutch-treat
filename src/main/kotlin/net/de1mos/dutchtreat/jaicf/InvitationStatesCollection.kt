package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.exceptions.EventNotFoundException
import net.de1mos.dutchtreat.exceptions.InvitationCodeNotFoundException
import net.de1mos.dutchtreat.services.InvitationService
import net.de1mos.dutchtreat.services.UserPreferencesService

class InvitationStatesCollection(
    private val invitationService: InvitationService,
    userPreferencesService: UserPreferencesService
) :
    BaseStatesCollection(userPreferencesService) {

    fun inviteUser(scenario: Scenario) {
        scenario.state("invite user") {
            globalActivators { regex("invite user") }
            action {
                wrapAction(this) {
                    val e = getUserEvent() ?: return@wrapAction
                    val code = invitationService.invite(e)
                    reactions.say("Send this code to user: $code")
                }
            }
        }
    }

    fun activateInvitationCode(scenario: Scenario) {
        scenario.state("activate invitation code") {
            globalActivators { regex("activate (?<val>[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}).*") }
            action {
                wrapAction(this) {
                    val code = getValFromRegex()
                    try {
                        val event = invitationService.applyInvitation(context.clientId, code)
                        reactions.say("You were successfully added to event ${event.name}")
                    } catch (e: InvitationCodeNotFoundException) {
                        reactions.say("Invitation code not found")
                    } catch (e: EventNotFoundException) {
                        reactions.say("event is gone...")
                    }
                }
            }
        }
    }
}