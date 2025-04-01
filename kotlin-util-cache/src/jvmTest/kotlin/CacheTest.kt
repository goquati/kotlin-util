import io.github.goquati.kotlin.util.Failure
import io.github.goquati.kotlin.util.Success
import io.github.goquati.kotlin.util.cache.Cache
import io.github.goquati.kotlin.util.cache.cacheBuilder
import io.github.goquati.kotlin.util.cache.nanosToInstant
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThanOrEqual
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.time.Duration.Companion.nanoseconds

class CacheTest {
    @Test
    fun testBasic(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> {
            capacity(5)
        }
        cache.put("putValue", 47) shouldBe 47
        cache.put("putFun") { 4 } shouldBe 4
        cache.get("getFun") { 3 } shouldBe 3
        cache.getCatching("getFunCatching") { Success(7) } shouldBe Success(7)
        cache.getCatching("getFunCatchingError") { Failure(1) } shouldBe Failure(1)
        val r1 = mapOf("putValue" to 47, "putFun" to 4, "getFun" to 3, "getFunCatching" to 7)
        cache.asMap() shouldBe r1
        cache.asDeferredMap().mapValues { it.value.await() } shouldBe r1

        cache.put("putValue", 470) shouldBe 470
        cache.put("putFun") { 40 } shouldBe 40
        cache.get("getFun") { 30 } shouldBe 3
        cache.getCatching("getFunCatching") { Success(70) } shouldBe Success(7)
        cache.getCatching("getFunCatchingError") { Failure(10) } shouldBe Failure(10)
        cache.asMap() shouldBe mapOf("putValue" to 470, "putFun" to 40, "getFun" to 3, "getFunCatching" to 7)

        cache.invalidate("getFun")
        cache.asMap() shouldBe mapOf("putValue" to 470, "putFun" to 40, "getFunCatching" to 7)
        cache.invalidateAll()
        cache.asMap() shouldBe mapOf()
        cache.asDeferredMap().mapValues { it.value.await() } shouldBe mapOf()
    }

    @Test
    fun testDefaultDispatcher(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> {
            defaultDispatcher(Dispatchers.IO)
        }

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
        val cache = cacheBuilder<String, Int> {
            defaultScope(CoroutineScope(Dispatchers.IO))
        }

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
        val cache = cacheBuilder<String, Int> {}
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
        val cache = cacheBuilder<String, Int> {}
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

    @Test
    fun testExpiryAfterWriteDuration(): Unit = runBlocking {
        var time = 0L
        val cache = cacheBuilder<String, Int> {
            expiryTimeBased(afterWrite = 10.nanoseconds)
            ticker { time }
        }
        cache.put("foo", 1) shouldBe 1
        time += 6L
        cache.getIfPresent("foo") shouldBe 1
        time += 6L
        cache.getIfPresent("foo") shouldBe null
        cache.put("foo", 3) shouldBe 3
        time += 11L
        cache.asMap() shouldBe mapOf()
    }

    @Test
    fun testExpiryAfterAccessDuration(): Unit = runBlocking {
        var time = 0L
        val cache = cacheBuilder<String, Int> {
            expiryTimeBased(afterAccess = 10.nanoseconds)
            ticker { time }
        }
        cache.put("foo", 1) shouldBe 1
        time += 6L
        cache.getIfPresent("foo") shouldBe 1
        time += 6L
        cache.getIfPresent("foo") shouldBe 1
        time += 11L
        cache.getIfPresent("foo") shouldBe null
        cache.put("foo", 1) shouldBe 1
        time += 11L
        cache.getIfPresent("foo") shouldBe null
        cache.asMap() shouldBe mapOf()
    }


    @Test
    fun testExpiryFunctionsSimple(): Unit = runBlocking {
        var time = 0L
        var nofWrite = 0
        var nofRead = 0
        val cache = cacheBuilder<String, Int> {
            expiryTimeBased(
                afterWrite = { _, _, _ ->
                    nofWrite++
                    10.nanoseconds
                },
                afterRead = { _, _, _, _ ->
                    nofRead++
                    10.nanoseconds
                },
            )
            ticker { time }
        }
        cache.put("foobar") { 47 }
        nofWrite shouldBe 1
        cache.get("foobar") { 48 } shouldBe 47
        nofRead shouldBe 1
        time += 1L
        cache.put("foobar", 48) shouldBe 48
        nofWrite shouldBe 2
        time += 11L
        cache.put("foobar", 49) shouldBe 49
        nofWrite shouldBe 3
        time += 1L
        cache.put("foobar") { 50 } shouldBe 50
        nofWrite shouldBe 4
        time += 11L
        cache.put("foobar") { 51 } shouldBe 51
        nofWrite shouldBe 5
    }

    @Test
    fun testExpiryFunctions(): Unit = runBlocking {
        var time = 0L
        var currentValue = 1
        val cache = cacheBuilder<String, Int> {
            expiryTimeBased(
                afterWrite = { key, value, currentTime ->
                    currentTime shouldBe time.nanosToInstant()
                    value shouldBe currentValue
                    if (key == "write") 10.nanoseconds else 5.nanoseconds
                },
                afterRead = { key, value, currentTime, _ ->
                    currentTime shouldBe time.nanosToInstant()
                    value shouldBe currentValue
                    if (key.startsWith("read")) 10.nanoseconds else 5.nanoseconds
                },
            )
            ticker { time }
        }

        // ------- write ------------------------
        cache.get("write") { currentValue } shouldBe currentValue
        time += 11L
        cache.getIfPresent("write") shouldBe null

        cache.put("write", currentValue) shouldBe currentValue
        time += 11L
        cache.getIfPresent("write") shouldBe null

        cache.get("write") { currentValue } shouldBe currentValue
        time += 6L
        cache.getIfPresent("write") shouldBe currentValue

        cache.put("write", currentValue) shouldBe currentValue
        time += 6L
        cache.getIfPresent("write") shouldBe currentValue

        cache.put("write", currentValue) shouldBe currentValue
        cache.getIfPresent("write") shouldBe currentValue // trigger afterRead
        time += 6L
        cache.getIfPresent("write") shouldBe null

        cache.put("write", currentValue) shouldBe currentValue
        currentValue = 2
        cache.put("write", 2) shouldBe 2 // trigger afterWrite
        time += 6L
        cache.getIfPresent("write") shouldBe 2
        cache.put("write") { 2 } shouldBe 2 // trigger afterWrite
        time += 6L
        cache.getIfPresent("write") shouldBe 2
        currentValue = 1

        // ------- read --------------------------
        cache.get("read") { currentValue } shouldBe currentValue
        time += 6L
        cache.getIfPresent("read") shouldBe null

        cache.put("read", currentValue) shouldBe currentValue
        time += 6L
        cache.getIfPresent("read") shouldBe null

        cache.put("read", currentValue) shouldBe currentValue
        cache.getIfPresent("read") shouldBe currentValue // trigger afterRead
        time += 6L
        cache.getIfPresent("read") shouldBe currentValue

        cache.put("read", currentValue) shouldBe currentValue
        cache.getIfPresent("read") shouldBe currentValue // trigger afterRead
        time += 11L
        cache.getIfPresent("read") shouldBe null

        cache.put("read-2", currentValue) shouldBe currentValue
        currentValue = 2
        cache.put("read-2", 2) shouldBe 2 // trigger afterWrite
        currentValue = 1
        time += 6L
        cache.getIfPresent("read-2") shouldBe null

        cache.put("read-2", currentValue) shouldBe currentValue
        currentValue = 2
        cache.put("read-2") { 2 } shouldBe 2 // trigger afterWrite
        currentValue = 1
        time += 6L
        cache.getIfPresent("read-2") shouldBe null
    }


    @Test
    fun testMaxSizeEviction(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> {
            expirySizeBased(maxSize = 3)
        }
        cache.put("k1") { 1 } shouldBe 1
        cache.put("k2") { 2 } shouldBe 2
        cache.put("k3") { 3 } shouldBe 3
        cache.put("k4") { 4 } shouldBe 4
        cache.cache.synchronous().cleanUp()
        cache.asMap() shouldHaveSize 3
    }


    @Test
    fun testMaxWeightEviction(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> {
            expirySizeBased(maxWeight = 7) { _, v -> v }
        }
        cache.put("k2") { 2 } shouldBe 2
        cache.put("k3") { 3 } shouldBe 3
        cache.cache.synchronous().cleanUp()
        cache.put("k4") { 4 } shouldBe 4
        cache.cache.synchronous().cleanUp()
        cache.asMap().values.sum() shouldBeGreaterThan 0
        cache.asMap().values.sum() shouldBeLessThanOrEqual 7

        cache.put("k8") { 8 } shouldBe 8
        cache.cache.synchronous().cleanUp()
        cache.getIfPresent("k8") shouldBe null
    }

    @Test
    fun removalListener(): Unit = runBlocking {
        var time = 0L
        val currentType = MutableStateFlow<Cache.RemovalCause?>(null)
        val cache = cacheBuilder<String, Int> {
            removalListener(Dispatchers.IO) { key, value, type ->
                key shouldBe "k1"
                value shouldBe 1
                currentType.update {
                    it shouldBe type
                    null
                }
            }
            expiryTimeBased(afterWrite = 10.nanoseconds)
            ticker { time }
        }

        cache.put("k1") { 1 } shouldBe 1
        currentType.update { Cache.RemovalCause.EXPIRED }
        time += 11L
        cache.getIfPresent("k1") shouldBe null
        currentType.first { it == null }

        cache.put("k1") { 1 } shouldBe 1
        currentType.update { Cache.RemovalCause.REPLACED }
        cache.put("k1") { 1 } shouldBe 1
        currentType.first { it == null }

        currentType.update { Cache.RemovalCause.EXPLICIT }
        cache.invalidate("k1")
        currentType.first { it == null }
    }

    @Test
    fun removalListenerSizedEvict(): Unit = runBlocking {
        val currentType = MutableStateFlow<Cache.RemovalCause?>(null)
        val cache = cacheBuilder<String, Int> {
            removalListener(CoroutineScope(Dispatchers.IO)) { key, value, type ->
                key shouldBe "k1"
                value shouldBe 1
                currentType.update {
                    it shouldBe type
                    null
                }
            }
            expirySizeBased(maxSize = 1)
        }
        cache.put("k1") { 1 } shouldBe 1
        currentType.update { Cache.RemovalCause.SIZE }
        cache.put("k2") { 2 } shouldBe 2
        currentType.first { it == null }
    }

    @Test
    fun testConcurrencyOfGet(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> { }
        val waiting = MutableStateFlow(0)
        val nofCalls = MutableStateFlow(0)

        coroutineScope {
            launch {
                waiting.update { it + 1 }
                waiting.first { it == 2 }
                cache.get("k2") { delay(200); nofCalls.update { it + 1 }; 2 } shouldBe 2
            }
            launch {
                waiting.update { it + 1 }
                waiting.first { it == 2 }
                cache.get("k2") { delay(200); nofCalls.update { it + 1 }; 2 } shouldBe 2
            }
        }
        nofCalls.value shouldBe 1
        cache.getIfPresent("k2") shouldBe 2
    }

    @Test
    fun testConcurrencyOfGetCatching(): Unit = runBlocking {
        val cache = cacheBuilder<String, Int> { }
        val waiting = MutableStateFlow(0)
        val nofCalls = MutableStateFlow(0)

        coroutineScope {
            launch {
                waiting.update { it + 1 }
                waiting.first { it == 2 }
                cache.getCatching("k2") { delay(200); nofCalls.update { it + 1 }; Success(2) }
            }
            launch {
                waiting.update { it + 1 }
                waiting.first { it == 2 }
                cache.getCatching("k2") { delay(200); nofCalls.update { it + 1 }; Success(2) }
            }
        }
        nofCalls.value shouldBe 1
        cache.getIfPresent("k2") shouldBe 2
    }
}