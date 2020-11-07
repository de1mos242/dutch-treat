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
        helper.query(query) responds "I don't know what you mean of $query"
    }
}