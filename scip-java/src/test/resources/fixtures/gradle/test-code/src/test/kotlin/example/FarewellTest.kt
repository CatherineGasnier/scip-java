package example

import org.junit.jupiter.api.Test

class FarewellTest {
    @Test
    fun saysBye() = check(Farewell().bye("Ada"))

    private fun check(farewell: String) {
        if (farewell != "Bye, Ada") throw AssertionError(farewell)
    }
}
