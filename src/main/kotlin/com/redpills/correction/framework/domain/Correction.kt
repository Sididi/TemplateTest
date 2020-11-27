package com.redpills.correction.framework.domain

import com.redpills.correction.framework.Corrector
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class Correction private constructor() {

    class BuilderDSL {
        internal val tasks = mutableListOf<Task>()
        internal var compileScript = String()

        fun withCompileScript(script: String) = also { compileScript = script }
        fun addOutputTask(task: OutputTaskDSL.() -> OutputTaskDSL) = also { tasks.add(task(OutputTaskDSL()).task) }
        fun addHttpTasks(task: HttpTaskSetDSL.() -> HttpTaskSetDSL) = also { tasks.add(task(HttpTaskSetDSL()).task) }

        class OutputTaskDSL {
            internal val task = OutputCorrectionTask()

            fun withLaunchScript(script: String) = also { task.script = script }
            fun withHint(hint: String) = also { task.hint = hint }
            fun expectStdoutResult(result: String) = also { task.expectedResult = result }.also { task.stream = 1 }
            fun expectStderrResult(result: String) = also { task.expectedResult = result }.also { task.stream = 2 }
            fun giveMarkOnSuccess(mark: Int) = also { task.mark = mark }
            fun setTimeoutMinutes(timeoutMinutes: Long) = also { task.timeoutMinutes = timeoutMinutes }
        }

        class HttpTaskSetDSL {
            internal val task = RuntimeCorrectionTaskSet()
            internal var port = 8080

            fun withLaunchScript(script: String) = also { task.script = script }
            fun definePort(port: Int) = also { this.port = port }
            fun waitBeforeTests(seconds: Long) = also { task.waitBeforeSeconds = seconds }
            fun waitBetweenEachTest(seconds: Long) = also { task.waitBetweenSeconds = seconds }
            fun waitAfterLastTest(seconds: Long) = also { task.waitAfterSeconds = seconds }

            fun addRequest(dsl: HttpTaskDSL.() -> HttpTaskDSL) = also { task.tasks.add(dsl(HttpTaskDSL(port)).task) }

            class HttpTaskDSL(internal val port: Int) {
                val task = HttpRestCorrectionTask().apply { request.port = this@HttpTaskDSL.port }

                fun withHint(hint: String) = also { task.hint = hint }
                fun giveMarkOnSuccess(mark: Int) = also { task.mark = mark }
                fun addRequestHeader(key: String, value: String) = also { task.request.headers[key] = value }
                fun withRequestUri(uri: String) = also { task.request.uri = uri }
                fun withRequestMethod(method: HttpMethod) = also { task.request.method = method }
                fun withRequestBody(body: String) = also { task.request.body = body }

                fun withExpectedResponseBody(body: String) = also { task.responseExpected.body = body }
                fun addExpectedResponseHeader(key: String, value: String) = also {
                    if (task.responseExpected.headers == null)
                        task.responseExpected.headers = HttpHeaders()
                    task.responseExpected.headers!!.set(key, value)
                }
            }
        }
    }

    companion object {
        fun newCorrection(builder: BuilderDSL.() -> Unit) {
            val config = BuilderDSL()
            builder(config)

            Corrector(config.tasks, config.compileScript).launch()
        }
    }
}