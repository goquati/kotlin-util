import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.util.cache.CacheDummy
import io.github.goquati.kotlin.util.successOrNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test

class CacheDummyTest {
    @Test
    fun testBasic(): Unit = runBlocking {
        val cache = CacheDummy<String, Int>()
        cache.put("putValue", 47) shouldBe 47
        cache.put("putFun") { 4 } shouldBe 4
        cache.get("getFun") { 3 } shouldBe 3
        cache.getIfPresent("putFun") shouldBe null
        cache.getIfPresent("getFun") shouldBe null
        cache.getIfPresent("getIfPresent") shouldBe null
        cache.getCatching("getFunCatching") { Success(7) } shouldBe Success(7)
        cache.getCatching("getFunCatchingError") { Failure(1) } shouldBe Failure(1)
        cache.asMap() shouldBe emptyMap()
        cache.asDeferredMap().mapValues { it.value.await() } shouldBe emptyMap()

        cache.put("putValue", 470) shouldBe 470
        cache.put("putFun") { 40 } shouldBe 40
        cache.get("getFun") { 30 } shouldBe 30
        cache.getCatching("getFunCatching") { Success(70) } shouldBe Success(70)
        cache.getCatching("getFunCatchingError") { Failure(10) } shouldBe Failure(10)
        cache.asMap() shouldBe emptyMap()

        cache.invalidate("getFun")
        cache.asMap() shouldBe emptyMap()
        cache.invalidateAll()
        cache.asMap() shouldBe emptyMap()
        cache.asDeferredMap().mapValues { it.value.await() } shouldBe emptyMap()
    }

    @Test
    fun testDefaultDispatcher(): Unit = runBlocking {
        val cache = CacheDummy<String, Int>(Dispatchers.IO)

        cache.get("get") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            1
        } shouldBe 1

        cache.getCatching("getCatching") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            Success(1)
        }.successOrNull shouldBe 1

        cache.put("put") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            1
        } shouldBe 1
    }

    @Test
    fun testDefaultScope(): Unit = runBlocking {
        val cache = CacheDummy<String, Int>(CoroutineScope(Dispatchers.IO))

        cache.get("get") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            1
        } shouldBe 1

        cache.getCatching("getCatching") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            Success(1)
        }.successOrNull shouldBe 1

        cache.put("put") {
            coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
            1
        } shouldBe 1
    }

    @Test
    fun testCurrentScopeDefault(): Unit = runBlocking {
        val cache = CacheDummy<String, Int>()
        withContext(Dispatchers.Default) {
            cache.get("get") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Default
                1
            } shouldBe 1

            cache.getCatching("getCatching") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Default
                Success(1)
            }.successOrNull shouldBe 1

            cache.put("put") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.Default
                1
            } shouldBe 1
        }
    }

    @Test
    fun testCurrentScopeIO(): Unit = runBlocking {
        val cache = CacheDummy<String, Int>()
        withContext(Dispatchers.IO) {
            cache.get("get") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
                1
            } shouldBe 1

            cache.getCatching("getCatching") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
                Success(1)
            }.successOrNull shouldBe 1

            cache.put("put") {
                coroutineContext[ContinuationInterceptor] shouldBe Dispatchers.IO
                1
            } shouldBe 1
        }
    }
}