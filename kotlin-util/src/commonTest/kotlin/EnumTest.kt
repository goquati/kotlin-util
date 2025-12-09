import de.quati.kotlin.util.valueOf
import de.quati.kotlin.util.valueOfOrNull
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class EnumTest {
    private enum class TestEnum { FOO, bar }

    @Test
    fun testValueOf() {
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("hello") }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("foo") }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("Foo") }
        valueOf<TestEnum>("FOO") shouldBe TestEnum.FOO
        valueOf<TestEnum>("bar") shouldBe TestEnum.bar
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("Bar") }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("BAR") }

        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("hello", ignoreCase = false) }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("foo", ignoreCase = false) }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("Foo", ignoreCase = false) }
        valueOf<TestEnum>("FOO", ignoreCase = false) shouldBe TestEnum.FOO
        valueOf<TestEnum>("bar", ignoreCase = false) shouldBe TestEnum.bar
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("Bar", ignoreCase = false) }
        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("BAR", ignoreCase = false) }

        shouldThrow<NoSuchElementException> { valueOf<TestEnum>("hello", ignoreCase = true) }
        valueOf<TestEnum>("foo", ignoreCase = true) shouldBe TestEnum.FOO
        valueOf<TestEnum>("Foo", ignoreCase = true) shouldBe TestEnum.FOO
        valueOf<TestEnum>("FOO", ignoreCase = true) shouldBe TestEnum.FOO
        valueOf<TestEnum>("bar", ignoreCase = true) shouldBe TestEnum.bar
        valueOf<TestEnum>("Bar", ignoreCase = true) shouldBe TestEnum.bar
        valueOf<TestEnum>("BAR", ignoreCase = true) shouldBe TestEnum.bar
    }

    @Test
    fun testValueOfOrNull() {
        valueOfOrNull<TestEnum>("hello") shouldBe null
        valueOfOrNull<TestEnum>("foo") shouldBe null
        valueOfOrNull<TestEnum>("Foo") shouldBe null
        valueOfOrNull<TestEnum>("FOO") shouldBe TestEnum.FOO
        valueOfOrNull<TestEnum>("bar") shouldBe TestEnum.bar
        valueOfOrNull<TestEnum>("Bar") shouldBe null
        valueOfOrNull<TestEnum>("BAR") shouldBe null

        valueOfOrNull<TestEnum>("hello", ignoreCase = false) shouldBe null
        valueOfOrNull<TestEnum>("foo", ignoreCase = false) shouldBe null
        valueOfOrNull<TestEnum>("Foo", ignoreCase = false) shouldBe null
        valueOfOrNull<TestEnum>("FOO", ignoreCase = false) shouldBe TestEnum.FOO
        valueOfOrNull<TestEnum>("bar", ignoreCase = false) shouldBe TestEnum.bar
        valueOfOrNull<TestEnum>("Bar", ignoreCase = false) shouldBe null
        valueOfOrNull<TestEnum>("BAR", ignoreCase = false) shouldBe null

        valueOfOrNull<TestEnum>("hello", ignoreCase = true) shouldBe null
        valueOfOrNull<TestEnum>("foo", ignoreCase = true) shouldBe TestEnum.FOO
        valueOfOrNull<TestEnum>("Foo", ignoreCase = true) shouldBe TestEnum.FOO
        valueOfOrNull<TestEnum>("FOO", ignoreCase = true) shouldBe TestEnum.FOO
        valueOfOrNull<TestEnum>("bar", ignoreCase = true) shouldBe TestEnum.bar
        valueOfOrNull<TestEnum>("Bar", ignoreCase = true) shouldBe TestEnum.bar
        valueOfOrNull<TestEnum>("BAR", ignoreCase = true) shouldBe TestEnum.bar
    }
}