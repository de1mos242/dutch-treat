package net.de1mos.dutchtreat

import com.justai.jaicf.reactions.text
import com.justai.jaicf.test.ScenarioTest
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
import org.hamcrest.MatcherAssert.assertThat
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
        helper.query("Get participants") responds "There is no participants yet, add someone"
        helper.query("Add participant Denis") responds "Great, you added Denis to your event"
        helper.query("Get participants") responds "Participants: Denis"
        helper.query("Add participant Nick") responds "Great, you added Nick to your event"
        helper.query("Get participants") responds "Participants: Denis, Nick"
    }
}