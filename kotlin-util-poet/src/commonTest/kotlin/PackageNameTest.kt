import com.squareup.kotlinpoet.ClassName
import de.quati.kotlin.util.poet.PackageName
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class PackageNameTest {

    @Test
    fun test() {
        PackageName("com.example.foo.bar") shouldBe PackageName("com.example.foo.bar")
        PackageName("com.example.foo.bar").name shouldBe "com.example.foo.bar"
        PackageName("com.example.foo.bar").toString() shouldBe "com.example.foo.bar"
        PackageName("com.example.foo.bar").parts shouldBe listOf("com", "example", "foo", "bar")
        PackageName("com.example.foo").plus("bar") shouldBe PackageName("com.example.foo.bar")
        PackageName("com.example.foo").className("Bar") shouldBe ClassName("com.example.foo", "Bar")
        PackageName("com.example.foo").className(listOf("Bar")) shouldBe ClassName("com.example.foo", "Bar")
    }
}