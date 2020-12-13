package net.de1mos.dutchtreat.jaicf

import com.justai.jaicf.model.scenario.Scenario
import net.de1mos.dutchtreat.config.AppInfoConfig
import net.de1mos.dutchtreat.services.UserPreferencesService

class SystemStatesCollection(private val appInfoConfig: AppInfoConfig, userPreferencesService: UserPreferencesService) :
    BaseStatesCollection(userPreferencesService) {

    fun fallback(scenario: Scenario) {
        scenario.fallback {
            reactions.say("I don't know what you mean with ${request.input}, send 'help' to see available commands")
        }
    }

    fun start(scenario: Scenario) {
        scenario.state("start") {
            globalActivators {
                event("/start")
                regex("/start")
            }
            action {
                wrapSentry {
                    reactions.say(
                        """
                                Hello, this is a Dutch Treat bot!
                                I'm here to help you with party events.
                                You can start with creating event just say "start event %event_name%".
                                To see the list of available commands just send "help".
                            """.trimIndent()
                    )
                }
            }
        }
    }

    fun version(scenario: Scenario) {
        scenario.state("version") {
            globalActivators { regex("version") }
            action { wrapSentry { reactions.say(appInfoConfig.version) } }
        }
    }

    fun help(scenario: Scenario) {
        scenario.state("help") {
            globalActivators {
                regex("help")
                intent("help")
            }
            action {
                wrapSentry {
                    reactions.say(
                        """
                        You can send me these common commands:
                        start event %Event name%
                        add participant %Participant name%
                        %Participant name% bought %Purchase description% for %Purchase cost% - to add purchase
                        %Participant name% gave %Participant name% %Transfer amount% - to transfer money between participants
                        get balance - show current event balance between participants
                        full help - to see all available commands
                    """.trimIndent()
                    )
                }
            }
        }
    }

    fun fullHelp(scenario: Scenario) {
        scenario.state("full help") {
            globalActivators {
                regex("full help")
                intent("full help")
            }
            action {
                wrapSentry {
                    reactions.say(
                        """
                        You can send me these commands:
                        -- Events
                        start event %Event name%
                        get current event
                        show events - see all events you're participated in
                        switch to event %Event name%
                        
                        -- Collaboration
                        invite user - generate invitation code, that you can send other user to add him to your event
                        activate %Code% - activate invitation code, so user will get full access to event and switch to it
                        
                        -- Participants
                        add participant %Participant name%
                        get participants - show all current participants
                        
                        -- Purchases
                        %Participant name% bought %Purchase description% for %Purchase cost% - to add purchase
                        Get purchases - show all purchases with their positions
                        Add %Participant name% as a consumer to purchase %Position number% - add concrete consumers to a purchase. Without it all participants consider as consumers
                        Add %Participant name% as a consumer to the last purchase - add concrete consumers to the last purchase. Without it all participants consider as consumers
                        Remove purchase %Position number% - remove purchase at position
                        
                        -- Transfers
                        %Participant name% gave %Participant name% %Transfer amount% - to transfer money between participants
                        Get transfers - show all transfers
                        Remove transfer %Position number% - remove transfer at position
                        
                        -- Balance
                        get balance - show current event balance between participants
                    """.trimIndent()
                    )
                }
            }
        }
    }
}