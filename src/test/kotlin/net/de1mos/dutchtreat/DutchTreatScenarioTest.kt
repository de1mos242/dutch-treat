package net.de1mos.dutchtreat

import com.justai.jaicf.reactions.text
import com.justai.jaicf.test.ScenarioTest
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.junit.Assert
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

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
        helper.withClientId("user0")
        helper.query(query) responds "I don't know what you mean with $query"
    }

    @Test
    fun `handle start`() {
         Assert.assertTrue(helper.event("/start").reactions.text?.response?.text?.startsWith("Hello") ?: false)
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
}
