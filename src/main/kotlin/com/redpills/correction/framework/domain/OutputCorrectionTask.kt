package com.redpills.correction.framework.domain

import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class OutputCorrectionTask(
    var script: String = "",
    var hint: String = "",
    var expectedResult: String = "",
    var stream: Int = 1,
    var mark: Int = 1,
    var timeoutMinutes: Long = 60
) : Task {
    var processOutput = String()

    override fun execute(dir: File?): List<TaskResult> {
        val proc = Runtime.getRuntime().exec(script, null, dir)

        val stream = when (this.stream) {
            1 -> proc.inputStream
            2 -> proc.errorStream
            else -> error("unknown stream ${this.stream} (must be 1: stdout or 2: stderr)")
        }

        readOutputAsync(stream)
        proc.waitFor(timeoutMinutes, TimeUnit.MINUTES)

        val mark = when (processOutput == expectedResult) {
            true -> mark
            else -> 0
        }

        return listOf(TaskResult(mark, this.mark, hint))
    }

    private fun readOutputAsync(stream: InputStream) {
        val reader = BufferedReader(InputStreamReader(stream))
        var line: String?

        while (reader.readLine().also { line = it } != null)
            processOutput += when (processOutput.isNotEmpty()) {
                true -> "\n$line"
                else -> line
            }
    }
}