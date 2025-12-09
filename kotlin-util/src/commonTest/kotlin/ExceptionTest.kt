import de.quati.kotlin.util.QuatiException
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class ExceptionTest {
    inline fun <reified T : QuatiException> check(
        status: Int,
        builder: (msg: String, e: Throwable?) -> T,
    ) {
        val t = Exception("base")

        val e1 = builder("foo", null)
        e1.status shouldBe status
        e1.msg shouldBe "foo"
        e1.cause shouldBe null

        val e2 = builder("bar", t)
        e2.status shouldBe status
        e2.msg shouldBe "bar"
        e2.cause shouldBe t

        val e3 = QuatiException.ofOrNull(status, "foo", null)!!
        e3::class shouldBe T::class
        e3.status shouldBe status
        e3.msg shouldBe "foo"
        e3.cause shouldBe null

        val e4 = QuatiException.ofOrNull(status, "bar", t)!!
        e4::class shouldBe T::class
        e4.status shouldBe status
        e4.msg shouldBe "bar"
        e4.cause shouldBe t
    }

    @Test
    fun testQuatiExceptions() {
        QuatiException.ofOrNull(200, "bar") shouldBe null

        check(400, QuatiException::BadRequest)
        check(401, QuatiException::Unauthorized)
        check(402, QuatiException::PaymentRequired)
        check(403, QuatiException::Forbidden)
        check(404, QuatiException::NotFound)
        check(405, QuatiException::MethodNotAllowed)
        check(406, QuatiException::NotAcceptable)
        check(407, QuatiException::ProxyAuthenticationRequired)
        check(408, QuatiException::RequestTimeout)
        check(409, QuatiException::Conflict)
        check(410, QuatiException::Gone)
        check(411, QuatiException::LengthRequired)
        check(412, QuatiException::PreconditionFailed)
        check(413, QuatiException::PayloadTooLarge)
        check(414, QuatiException::UriTooLong)
        check(415, QuatiException::UnsupportedMediaType)
        check(416, QuatiException::RequestedRangeNotSatisfiable)
        check(417, QuatiException::ExpectationFailed)
        check(418, QuatiException::IAmATeapot)
        check(422, QuatiException::UnprocessableEntity)
        check(423, QuatiException::Locked)
        check(424, QuatiException::FailedDependency)
        check(425, QuatiException::TooEarly)
        check(426, QuatiException::UpgradeRequired)
        check(428, QuatiException::PreconditionRequired)
        check(429, QuatiException::TooManyRequests)
        check(431, QuatiException::RequestHeaderFieldsTooLarge)
        check(451, QuatiException::UnavailableForLegalReasons)
        check(500, QuatiException::InternalServerError)
        check(501, QuatiException::NotImplemented)
        check(502, QuatiException::BadGateway)
        check(503, QuatiException::ServiceUnavailable)
        check(504, QuatiException::GatewayTimeout)
        check(505, QuatiException::HttpVersionNotSupported)
        check(506, QuatiException::VariantAlsoNegotiates)
        check(507, QuatiException::InsufficientStorage)
        check(508, QuatiException::LoopDetected)
        check(509, QuatiException::BandwidthLimitExceeded)
        check(510, QuatiException::NotExtended)
        check(511, QuatiException::NetworkAuthenticationRequired)
    }
}