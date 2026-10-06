import de.quati.kotlin.util.poet.makeDifferent
import de.quati.kotlin.util.poet.toAsciiIdentifierLike
import de.quati.kotlin.util.poet.toCamelCase
import de.quati.kotlin.util.poet.toKebabCase
import de.quati.kotlin.util.poet.toSnakeCase
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NamingUtilTest {
    @Test
    fun testToCamelCase() {
        "hello world".toCamelCase(capitalized = false) shouldBe "helloWorld"
        "hello world".toCamelCase(capitalized = true) shouldBe "HelloWorld"

        "hello_world-test".toCamelCase(capitalized = false) shouldBe "helloWorldTest"
        "hello_world-test".toCamelCase(capitalized = true) shouldBe "HelloWorldTest"

        "".toCamelCase(capitalized = false) shouldBe "_empty"
        "1abc".toCamelCase(capitalized = false) shouldBe "_1abc"
        "-abc".toCamelCase(capitalized = false) shouldBe "abc"

        // Punctuation separates words
        "filter[field]".toCamelCase(capitalized = false) shouldBe "filterField"
        "scope.project.id".toCamelCase(capitalized = false) shouldBe "scopeProjectId"
        "binding:host_id".toCamelCase(capitalized = true) shouldBe "BindingHostId"
        "application/json".toCamelCase(capitalized = false) shouldBe "applicationJson"
        "hi@there!".toCamelCase(capitalized = false) shouldBe "hiThere"
        "[field]".toCamelCase(capitalized = false) shouldBe "field"

        // Diacritics still transliterate inside a word
        "Müller.straße".toCamelCase(capitalized = false) shouldBe "muellerStrasse"
        "Crème Brûlée".toCamelCase(capitalized = true) shouldBe "CremeBrulee"
    }

    @Test
    fun testToKebabCase() {
        "Hello World".toKebabCase() shouldBe "hello-world"
        "hello_world-test".toKebabCase() shouldBe "hello-world-test"

        "".toKebabCase() shouldBe "_empty"
        "1abc".toKebabCase() shouldBe "_1abc"
        "-abc".toKebabCase() shouldBe "abc"

        "filter[field]".toKebabCase() shouldBe "filter-field"
        "scope.project.id".toKebabCase() shouldBe "scope-project-id"
    }

    @Test
    fun testToSnakeCase() {
        "Hello World".toSnakeCase() shouldBe "hello_world"
        "Hello World".toSnakeCase(uppercase = true) shouldBe "HELLO_WORLD"
        "hello_world-test".toSnakeCase() shouldBe "hello_world_test"

        "".toSnakeCase() shouldBe "_empty"
        "1abc".toSnakeCase() shouldBe "_1abc"
        "-abc".toSnakeCase() shouldBe "abc"

        "filter[field]".toSnakeCase(uppercase = true) shouldBe "FILTER_FIELD"
        "router:external".toSnakeCase() shouldBe "router_external"
    }

    @Test
    fun testMakeDifferent() {
        "name".makeDifferent(listOf("other")) shouldBe "name"
        "name".makeDifferent(listOf("name")) shouldBe "name_1"
        "name".makeDifferent(listOf("name", "name_1", "name_2")) shouldBe "name_3"

        "name".makeDifferent(listOf("other"), "") shouldBe "name"
        "name".makeDifferent(listOf("name"), "") shouldBe "name1"
        "name".makeDifferent(listOf("name", "name1", "name2"), "") shouldBe "name3"
    }

    @Test
    fun testToAsciiIdentifierLike() {
        // German specifics
        "Müller".toAsciiIdentifierLike() shouldBe "Mueller"
        "müller".toAsciiIdentifierLike() shouldBe "mueller"
        "straße".toAsciiIdentifierLike() shouldBe "strasse"
        "Österreich".toAsciiIdentifierLike() shouldBe "Oesterreich"
        "Über-cool".toAsciiIdentifierLike() shouldBe "Ueber-cool"

        // General diacritics (via mapping / combining mark stripping)
        "café".toAsciiIdentifierLike() shouldBe "cafe"
        "Crème Brûlée".toAsciiIdentifierLike() shouldBe "Creme Brulee"
        "Łódź".toAsciiIdentifierLike() shouldBe "Lodz"

        // Drops unsupported punctuation/emojis but keeps spaces/_/-
        "hi@there!".toAsciiIdentifierLike() shouldBe "hithere"
        "a🙂b".toAsciiIdentifierLike() shouldBe "ab"
        "a_b-c d".toAsciiIdentifierLike() shouldBe "a_b-c d"

        "ÀàÇçÐðÈèĜĝĤĥÌìĴĵĶķĹĺÑñÒòŔŕŚśÞþÙùÝýŹź".toAsciiIdentifierLike() shouldBe "AaCcDdEeGgHhIiJjKkLlNnOoRrSsTtUuYyZz"

        "Æ".toAsciiIdentifierLike() shouldBe  "Ae"
        "æ".toAsciiIdentifierLike() shouldBe  "ae"
        "Œ".toAsciiIdentifierLike() shouldBe  "Oe"
        "œ".toAsciiIdentifierLike() shouldBe  "oe"
    }
}
