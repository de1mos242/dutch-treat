package net.de1mos.dutchtreat

import com.justai.jaicf.reactions.text
import com.justai.jaicf.test.ScenarioTest
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
class DutchTreatScenarioTest() {

    @Autowired
    lateinit var scenario: DutchTreatScenario
    lateinit var helper: ScenarioTest

    @BeforeEach
    fun init() {
        helper = ScenarioTest(scenario.bot)
        helper.init()
    }

    @Test
    fun `catch all test`() {
        val query = "i don't know"
        helper.query(query) responds "I don't know what you mean with $query, send 'help' to see available commands"
    }

    @Test
    fun `handle start`() {
        Assertions.assertTrue(helper.event("/start").reactions.text?.response?.text?.startsWith("Hello") ?: false)
    }

    @Test
    fun `handle help`() {
        Assertions.assertTrue(helper.query("help").reactions.text?.response?.text?.startsWith("You can send me these common commands")
                ?: false)
    }

    @Test
    fun `handle full help`() {
        Assertions.assertTrue(helper.query("full help").reactions.text?.response?.text?.startsWith("You can send me these commands")
                ?: false)
    }

    @Test
    fun `handle version`() {
        helper.query("version") responds "0.0.2-SNAPSHOT"
    }

    @Test
    fun `create event`() {
        val eventName = "party"
        helper.query("get current event") responds "There is no current event, but you can create a new one"
        helper.query("Start event $eventName") responds "Great, you created an event '$eventName'"
        helper.query("get current event") responds "Current event is: $eventName"
    }

    @Test
    fun `add participant`() {
        helper.query("Start event test")
        helper.query("Get participants") responds "There are no participants yet, add someone"
        helper.query("Add participant Denis") responds "Great, you added Denis to your event"
        helper.query("Get participants") responds "Participants: Denis"
        helper.query("Add participant Nick") responds "Great, you added Nick to your event"
        helper.query("Get participants") responds "Participants: Denis, Nick"
    }

    @Test
    fun `add purchases`() {
        helper.query("Start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Get purchases") responds "There are no purchases yet, add a new one"
        helper.query("Denis bought meat for 400 rubles") responds "Great, added Denis purchase for 400,00"
        helper.query("Nick bought beer for 230.03") responds "Great, added Nick purchase for 230,03"
        helper.query("Sam bought vodka for 550") responds "There is no participant with name Sam, add him or her before"
        helper.query("Get purchases") responds """
            Purchases list:
            1. Denis bought meat for 400,00
            2. Nick bought beer for 230,03
        """.trimIndent()
    }

    @Test
    fun `remove purchase`() {
        helper.query("Start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Get purchases")
        helper.query("Denis bought meat for 400 rubles")
        helper.query("Nick bought beer for 230.03")
        helper.query("Get purchases") responds """
            Purchases list:
            1. Denis bought meat for 400,00
            2. Nick bought beer for 230,03
        """.trimIndent()
        helper.query("Remove purchase 1") responds "Purchase meat removed"
        helper.query("Get purchases") responds """
            Purchases list:
            1. Nick bought beer for 230,03
        """.trimIndent()
        helper.query("Remove purchase 4") responds "Purchase with position 4 not found"
    }

    @Test
    fun `show balance`() {
        helper.query("Start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Add participant Linda")
        helper.query("get balance") responds """
            Current balance
            Denis owes nobody and nobody owes him or her
            Nick owes nobody and nobody owes him or her
            Linda owes nobody and nobody owes him or her
        """.trimIndent()
        helper.query("Denis bought meat for 400")
        helper.query("Nick bought beer for 230.04")
        helper.query("Linda gave Nick 15")
        helper.query("Linda gave Denis 150")
        helper.query("get balance") responds """
            Current balance
            Denis should get 39,99
            Nick should get 5,03
            Linda should give 45,01
        """.trimIndent()
        helper.query("Linda gave Nick 5")
        helper.query("Linda gave Denis 40")
        helper.query("get balance") responds """
            Current balance
            Denis owes nobody and nobody owes him or her
            Nick owes nobody and nobody owes him or her
            Linda owes nobody and nobody owes him or her
        """.trimIndent()
    }

    @Test
    fun `transfer money`() {
        helper.query("start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Add participant Linda")
        helper.query("Get transfers") responds "There are no transfers yet, add a new one"
        helper.query("Nick gave Linda 50") responds "Great, sent 50,00 from Nick to Linda"
        helper.query("Denis gave Nick 150.45") responds "Great, sent 150,45 from Denis to Nick"
        helper.query("Sam gave Nick 150.45") responds "There is no participant with name Sam, add him or her before"
        helper.query("Denis gave Sam 150.45") responds "There is no participant with name Sam, add him or her before"
        helper.query("get transfers") responds """
            Transfers list:
            1. Nick gave Linda 50,00
            2. Denis gave Nick 150,45
        """.trimIndent()
    }

    @Test
    fun `remove transfer`() {
        helper.query("start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Add participant Linda")
        helper.query("Nick gave Linda 50")
        helper.query("Denis gave Nick 150.45")
        helper.query("Sam gave Nick 150.45")
        helper.query("Denis gave Sam 150.45")
        helper.query("get transfers") responds """
            Transfers list:
            1. Nick gave Linda 50,00
            2. Denis gave Nick 150,45
        """.trimIndent()
        helper.query("remove transfer 2") responds "Transfer 'Denis gave Nick 150,45' removed"
        helper.query("remove transfer 4") responds "Transfer with position 4 not found"
        helper.query("get transfers") responds """
            Transfers list:
            1. Nick gave Linda 50,00
        """.trimIndent()
    }

    @Test
    fun `add consumers`() {
        helper.query("start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Add participant Linda")
        helper.query("Add participant Vanessa")
        helper.query("Add Denis as a consumer to the last purchase") responds "There are no purchases yet, add a new one"
        helper.query("Denis bought meat for 400 rubles")
        helper.query("Add Denis as a consumer to the last purchase") responds "Great, I've added Denis as a consumer for meat"
        helper.query("Add Sam as a consumer to the last purchase") responds "There is no participant with name Sam, add him or her before"
        helper.query("Nick bought beer for 230.03")
        helper.query("Vanessa bought vine for 75")
        helper.query("Add vanessa as a consumer to the last purchase") responds "Great, I've added vanessa as a consumer for vine"
        helper.query("Get purchases") responds """
            Purchases list:
            1. Denis bought meat for 400,00 for Denis
            2. Nick bought beer for 230,03
            3. Vanessa bought vine for 75,00 for Vanessa
        """.trimIndent()
        helper.query("Add Nick as a consumer to purchase 1") responds "Great, I've added Nick as a consumer for meat"
        helper.query("Add Nick as a consumer to purchase 4") responds "Purchase with position 4 not found"
        helper.query("Add Denis as a consumer to purchase 2") responds "Great, I've added Denis as a consumer for beer"
        helper.query("Add Linda as a consumer to purchase 2") responds "Great, I've added Linda as a consumer for beer"
        helper.query("Add Nick as a consumer to purchase 2") responds "Great, I've added Nick as a consumer for beer"
        helper.query("Get purchases") responds """
            Purchases list:
            1. Denis bought meat for 400,00 for Denis and Nick
            2. Nick bought beer for 230,03 for Denis, Linda and Nick
            3. Vanessa bought vine for 75,00 for Vanessa
        """.trimIndent()
        helper.query("get balance") responds """
            Current balance
            Denis should get 123,32
            Nick should give 46,65
            Linda should give 76,68
            Vanessa owes nobody and nobody owes him or her
        """.trimIndent()
    }

    @Test
    fun `create several events and switch between them`() {
        helper.query("show events") responds "There are no events yet, try to start a new one"
        helper.query("start event test")
        helper.query("start event test2")
        helper.query("show events") responds """
            Your events:
            1. test
            2. test2
        """.trimIndent()
        helper.query("get current event") responds "Current event is: test2"
        helper.query("switch to event test") responds "Switched to event test"
        helper.query("switch to event test4") responds "There is no event test4, but you could start it"
        helper.query("start event test3")
        helper.query("show events") responds """
            Your events:
            1. test
            2. test2
            3. test3
        """.trimIndent()
    }

    @Test
    fun `invite users to event`() {
        val user1 = UUID.randomUUID().toString()
        val user2 = UUID.randomUUID().toString()
        val user3 = UUID.randomUUID().toString()

        helper.withClientId(user1)
        helper.query("show events") responds "There are no events yet, try to start a new one"
        helper.query("start event test")
        helper.query("show events") responds """
            Your events:
            1. test
        """.trimIndent()

        helper.withClientId(user2)
        helper.query("show events") responds "There are no events yet, try to start a new one"
        helper.withClientId(user3)
        helper.query("show events") responds "There are no events yet, try to start a new one"

        helper.withClientId(user1)
        val invitation = helper.query("invite user").reactions.text?.response?.text!!
        val regex = "Send this code to user: ([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})".toRegex()
        Assertions.assertTrue(regex.matches(invitation))
        val code = regex.find(invitation)?.groups?.get(1)!!.value

        helper.withClientId(user2)
        helper.query("start event test2")
        helper.query("show events") responds """
            Your events:
            1. test2
        """.trimIndent()
        helper.query("activate $code") responds "You were successfully added to event test"
        helper.query("show events") responds """
            Your events:
            1. test
            2. test2
        """.trimIndent()

        helper.withClientId(user3)
        helper.query("show events") responds "There are no events yet, try to start a new one"

        helper.withClientId(user1)
        helper.query("show events") responds """
            Your events:
            1. test
        """.trimIndent()

        helper.withClientId(user3)
        helper.query("activate $code") responds "Invitation code not found"
    }

    @Test
    fun `complex demo test`() {
        val user1 = UUID.randomUUID().toString()
        val user2 = UUID.randomUUID().toString()

        helper.withClientId(user1)
        helper.query("Start event test")
        helper.query("Add participant Denis")
        helper.query("Add participant Nick")
        helper.query("Add participant Linda")
        helper.query("get balance") responds """
            Current balance
            Denis owes nobody and nobody owes him or her
            Nick owes nobody and nobody owes him or her
            Linda owes nobody and nobody owes him or her
        """.trimIndent()

        val invitation = helper.query("invite user").reactions.text?.response?.text!!
        val regex = "Send this code to user: ([a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12})".toRegex()
        Assertions.assertTrue(regex.matches(invitation))
        val code = regex.find(invitation)?.groups?.get(1)!!.value

        helper.withClientId(user2)
        helper.query("activate $code")

        helper.query("Denis bought meat for 400")
        helper.query("Linda bought ice cream for 333")
        helper.query("Nick bought beer for 230.04")
        helper.query("Remove purchase 2")
        helper.query("Linda gave Nick 15")
        helper.query("Linda gave Denis 150")
        helper.query("get balance") responds """
            Current balance
            Denis should get 39,99
            Nick should get 5,03
            Linda should give 45,01
        """.trimIndent()

        helper.withClientId(user1)
        helper.query("get balance") responds """
            Current balance
            Denis should get 39,99
            Nick should get 5,03
            Linda should give 45,01
        """.trimIndent()
        helper.query("Linda gave Nick 5")
        helper.query("Linda gave Denis 50")
        helper.query("remove transfer 4")

        helper.withClientId(user2)
        helper.query("Linda gave Denis 40")
        helper.query("get balance") responds """
            Current balance
            Denis owes nobody and nobody owes him or her
            Nick owes nobody and nobody owes him or her
            Linda owes nobody and nobody owes him or her
        """.trimIndent()
    }
}
