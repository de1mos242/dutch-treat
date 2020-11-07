package net.de1mos.dutchtreat

import com.justai.jaicf.test.ScenarioTest
import net.de1mos.dutchtreat.jaicf.DutchTreatScenario
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
    fun `create event`() {
        val eventName = "party"
        helper.query("get current event") responds "There is no current event, but you can create a new one"
        helper.query("Start event $eventName") responds "Great, you created an event '$eventName'"
        helper.query("get current event") responds "Current event is: $eventName"
    }
}