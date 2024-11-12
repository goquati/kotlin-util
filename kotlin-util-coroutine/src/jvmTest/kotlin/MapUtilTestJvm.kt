import io.github.goquati.kotlin.util.coroutine.getOrPutAsync
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test

class MapUtilTestJvm {
    @Test
    fun testGetOrPutAsync1(): TestResult = runTest {
        val data = ConcurrentHashMap<String, CompletableFuture<Int>>()
        data.getOrPutAsync("k1") { 1 } shouldBe 1
        data.getOrPutAsync("k2") { 2 } shouldBe 2
        data.mapValues { it.value.await() } shouldBe mapOf("k1" to 1, "k2" to 2)
    }

    @Test
    fun testGetOrPutAsync2(): TestResult = runTest { // ensure a key is only once computed
        val data = ConcurrentHashMap<String, CompletableFuture<Int>>()
        val nofComputation = AtomicInteger(0)
        coroutineScope {
            launch {
                data.getOrPutAsync("k1") {
                    delay(500)
                    nofComputation.incrementAndGet()
                    1
                } shouldBe 1
            }
            launch {
                delay(200)
                data.getOrPutAsync("k1") {
                    nofComputation.incrementAndGet()
                    2
                } shouldBe 1
            }
        }
        nofComputation.toInt() shouldBe 1
        data.mapValues { it.value.await() } shouldBe mapOf("k1" to 1)
    }
}