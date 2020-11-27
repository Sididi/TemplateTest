package com.redpills.correction.framework.domain

import java.io.File

class RuntimeCorrectionTaskSet(
    var script: String = "",
    var waitBeforeSeconds: Long = 0,
    var waitBetweenSeconds: Long = 0,
    var waitAfterSeconds: Long = 0,
    var tasks: MutableList<RuntimeCorrectionTask> = mutableListOf()
): Task {
    override fun execute(dir: File?): List<TaskResult> {
        val proc = Runtime.getRuntime().exec(script)

        Thread.sleep(waitBeforeSeconds * 1000)

        val results = tasks.map {
            if (waitBetweenSeconds > 0)
                Thread.sleep(waitBeforeSeconds * 1000)
            it.execute(proc)
        }

        Thread.sleep(waitAfterSeconds * 1000)
        proc.destroyForcibly().waitFor()

        return results
    }
}