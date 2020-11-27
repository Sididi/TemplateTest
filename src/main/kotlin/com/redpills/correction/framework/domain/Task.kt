package com.redpills.correction.framework.domain

import java.io.File

interface Task {
    fun execute(dir: File? = null): List<TaskResult>
}

interface RuntimeTask {
    fun execute(process: Process? = null): TaskResult
}