package com.redpills.correction.framework.domain

import com.redpills.correction.framework.helpers.Http
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod

class HttpRestCorrectionTask(
    hint: String = "",
    mark: Int = 1,
    var request: HttpRequest = HttpRequest(),
    var responseExpected: HttpResponse = HttpResponse()
): RuntimeCorrectionTask(hint, mark) {
    override fun execute(process: Process?): TaskResult {
        val response = Http.request(request.method, "http://localhost:${request.port}${request.uri}")
            .setHeaders(request.headers)
            .withBody(request.body)
            .toHttpResponse()

        var validResponse = true

        if (responseExpected.body != null)
            validResponse = response.body == responseExpected.body

        if (responseExpected.headers != null)
            validResponse = validResponse && responseExpected.headers!!.all { response.headers[it.key] == it.value }

        val mark = when (validResponse) {
            true -> this.mark
            else -> 0
        }

        return TaskResult(mark, hint)
    }
}

class HttpRequest(
    var port: Int = 8080,
    var method: HttpMethod = HttpMethod.GET,
    var uri: String = "",
    var body: String = "",
    var headers: HttpHeaders = HttpHeaders()
)

data class HttpResponse(
    var body: String? = null,
    var headers: HttpHeaders? = null
)