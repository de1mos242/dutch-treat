package net.de1mos.dutchtreat

import com.justai.jaicf.test.ScenarioTest
import net.de1mos.dutchtreat.endpoints.bot
import org.junit.jupiter.api.Test

class DutchTreatScenarioTest : ScenarioTest(bot) {

    @Test
    fun `catch all test`() {
        val query = "i don't know"
        query(query) responds "I don't know what you mean of $query"
    }
}