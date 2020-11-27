package com.redpills.correction.framework

import com.redpills.correction.framework.dto.ContainerIdentityDTO
import com.redpills.correction.framework.dto.WorkerTestResultDTO
import com.redpills.correction.framework.helpers.GitRepository
import com.redpills.correction.framework.helpers.Http
import java.io.BufferedReader
import java.io.InputStreamReader

open class Worker {
    private val serviceUrl = "http://localhost:8080/worker"

    protected val containerId: ContainerIdentityDTO
    protected val studentRepository: GitRepository

    init {
        val cmd = arrayOf(
            "/bin/sh",
            "-c",
            "head -1 /proc/self/cgroup|cut -d/ -f3"
        )

        val proc = Runtime.getRuntime().exec(cmd)
        val reader = BufferedReader(InputStreamReader(proc.inputStream))
        var line: String?
        var containerId = ""

        while (reader.readLine().also { line = it } != null)
            containerId += line

        println("CONTAINER ID IS $containerId")
        this.containerId = ContainerIdentityDTO(containerId)
        this.studentRepository = Http.post("$serviceUrl/init", containerId).toResponse(GitRepository::class.java)
            ?: error("internal error while trying to retrieve student repository")
    }

    protected fun notifyCorrectionStarted() =
        Http.post("$serviceUrl/tests/start", containerId).toResponse()

    protected fun notifyCorrectionEnded() =
        Http.post("$serviceUrl/tests/finished", containerId).toResponse()

    protected fun publishResult(results: WorkerTestResultDTO) =
        Http.post("$serviceUrl/tests/result", results).toResponse()
}