import io.github.goquati.kotlin.coroutine.util.getAndResetList
import io.github.goquati.kotlin.coroutine.util.getAndResetSet
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class StateFlowUtilTest {
    @Test
    fun testGetAndRestList(): TestResult = runTest {
        val state = MutableStateFlow(listOf<Int>())
        launch {
            state.getAndResetList() shouldBe listOf(1, 2, 3)
            state.getAndResetList() shouldBe listOf(4, 5, 6)
        }
        launch {
            state.update { it + listOf(1, 2, 3) }
            state.first { it.isEmpty() }
            state.update { it + listOf(4, 5, 6) }
            state.first { it.isEmpty() }
        }
    }

    @Test
    fun testGetAndRestSet(): TestResult = runTest {
        val state = MutableStateFlow(setOf<Int>())
        launch {
            state.getAndResetSet() shouldBe setOf(1, 2, 3)
            state.getAndResetSet() shouldBe setOf(4, 5, 6)
        }
        launch {
            state.update { it + setOf(1, 2, 3) }
            state.first { it.isEmpty() }
            state.update { it + setOf(4, 5, 6) }
            state.first { it.isEmpty() }
        }
    }
}