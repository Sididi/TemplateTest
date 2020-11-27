import com.redpills.correction.framework.domain.Correction
import org.springframework.http.HttpMethod

class DslExample {
    fun dsl() {
        Correction.newCorrection {
            withCompileScript("cmake . && cd build && make")

            addOutputTask {
                withLaunchScript("./sample -arg1 -arg2")
                withHint("test description")
                expectStderrResult("sample error")
                giveMarkOnSuccess(10)
                setTimeoutMinutes(2)
            }

            addOutputTask {
                withLaunchScript("./sample -arg1 -arg2")
                withHint("test description")
                expectStderrResult("sample error")
                giveMarkOnSuccess(10)
                setTimeoutMinutes(2)
            }

            addHttpTasks {
                withLaunchScript("./webserver -p 8080")
                definePort(8080)
                waitBeforeTests(60) // wait webserver initialized
                waitBetweenEachTest(1) // sugardaddy
                waitAfterLastTest(10)

                addRequest {
                    withHint("Test /api/data")
                    giveMarkOnSuccess(10)
                    withRequestUri("/api/data")
                    withRequestMethod(HttpMethod.POST)
                    withRequestBody("{\"foo\": \"bar\"}")
                    addRequestHeader("Content-Type", "application/json")

                    addExpectedResponseHeader("status", "200")
                    withExpectedResponseBody("{\"data\": \"hi\"}")
                }

                addRequest {
                    withHint("Another Test etc...")
                }
            }
        }
    }
}