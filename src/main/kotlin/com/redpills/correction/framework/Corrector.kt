package com.redpills.correction.framework

import com.redpills.correction.framework.domain.Task
import com.redpills.correction.framework.dto.WorkerInnerTestResultDTO
import com.redpills.correction.framework.dto.WorkerTestResultDTO
import com.redpills.correction.framework.dto.WorkerTestResultStateDTO
import com.redpills.correction.framework.helpers.Git
import java.io.File

class Corrector(
    val tasks: List<Task>,
    val compileScript: String
): Worker() {
    val results = WorkerTestResultDTO(containerId, "Tests", "", mutableListOf())
    lateinit var studentDirectory: File

    fun launch() {
        println("Cloning student repository")
        cloneRepository()

        println("Compiling student project")
        compileProject()

        println("Notifying correction service: tests are starting")
        notifyCorrectionStarted()

        println("Executing tests")
        executeTests()

        println("Publishing test results")
        publishResult(results)

        println("Notifying correction service: tests ended")
        notifyCorrectionEnded()
    }

    private fun executeTests() {
        var markObtained = 0
        var markMax = 0

        val marks = tasks.flatMap { it.execute(studentDirectory) }.map {
            val status: WorkerTestResultStateDTO;
            markMax += it.mark;

            when (it.mark) {
                0 -> status = WorkerTestResultStateDTO.FAILED
                else -> {
                    status = WorkerTestResultStateDTO.PASSED
                    markObtained += it.mark
                }
            }

            WorkerInnerTestResultDTO(it.hint, mutableListOf(), status, it.mark)
        }

        results.mark = "$markObtained/$markMax"

        results.children.addAll(marks)
    }

    private fun compileProject() {
        Runtime.getRuntime().exec(compileScript, null, studentDirectory).waitFor()
    }

    private fun cloneRepository() {
        this.studentDirectory = Git.installOrUpdate(studentRepository)
    }
}