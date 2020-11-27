import com.redpills.correction.framework.domain.HttpRequest
import com.redpills.correction.framework.domain.HttpResponse
import com.redpills.correction.framework.domain.HttpRestCorrectionTask
import io.ktor.application.call
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.stop
import io.ktor.server.netty.Netty
import org.junit.Test
import org.springframework.http.HttpMethod
import java.util.concurrent.TimeUnit

class HttpTest {
    fun httpTest() {
        val server = server(8080)
        val expectedMark = 20

        val test = HttpRestCorrectionTask().apply {
            hint = "GET hello world result"
            mark = expectedMark
            request = HttpRequest().apply {
                method = HttpMethod.GET
                uri = "/test"
                port = 8080
            }
            responseExpected = HttpResponse().apply {
                body = "hello world"
            }
        }

        val result = test.execute()
        assert(result.mark == expectedMark)

        server.stop(1L, 1L, TimeUnit.SECONDS)
    }

    private fun server(port: Int) = embeddedServer(Netty, port = port) {
            routing {
                get("/test") {
                    call.respondText("hello world")
                }
            }
        }.apply {
            start(wait = false)
            Thread.sleep(5000)
        }
}