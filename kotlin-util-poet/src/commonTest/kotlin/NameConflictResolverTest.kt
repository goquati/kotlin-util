import de.quati.kotlin.util.poet.NameConflictResolver
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NameConflictResolverTest {
    @Test
    fun base() {
        NameConflictResolver().apply {
            resolve("foo") shouldBe "foo"
            resolve("foo") shouldBe "foo_1"
        }
        NameConflictResolver(forbidden = listOf("foo")).apply {
            resolve("foo") shouldBe "foo_1"
        }
        NameConflictResolver().apply {
            forbid("bar")
            resolve("bar") shouldBe "bar_1"
        }
        NameConflictResolver().apply {
            forbidAll(listOf("a", "b", "c"))
            resolve("a") shouldBe "a_1"
            resolve("b") shouldBe "b_1"
            resolve("c") shouldBe "c_1"
        }
        NameConflictResolver(forbidden = listOf("x")).apply {
            resolve("x") shouldBe "x_1"
            resolve("x") shouldBe "x_2"
            resolve("x") shouldBe "x_3"
        }
        NameConflictResolver().apply {
            resolve("class") shouldBe "class_1"
        }
    }

    @Test
    fun baseWithSeparator() {
        NameConflictResolver(separator = "").apply {
            resolve("foo") shouldBe "foo"
            resolve("foo") shouldBe "foo1"
        }
        NameConflictResolver(separator = "", forbidden = listOf("foo")).apply {
            resolve("foo") shouldBe "foo1"
        }
        NameConflictResolver(separator = "").apply {
            forbid("bar")
            resolve("bar") shouldBe "bar1"
        }
        NameConflictResolver(separator = "").apply {
            forbidAll(listOf("a", "b", "c"))
            resolve("a") shouldBe "a1"
            resolve("b") shouldBe "b1"
            resolve("c") shouldBe "c1"
        }
        NameConflictResolver(separator = "", forbidden = listOf("x")).apply {
            resolve("x") shouldBe "x1"
            resolve("x") shouldBe "x2"
            resolve("x") shouldBe "x3"
        }
        NameConflictResolver(separator = "").apply {
            resolve("class") shouldBe "class1"
        }
    }
}