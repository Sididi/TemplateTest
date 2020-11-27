package com.redpills.correction.template

import com.redpills.correction.framework.domain.Correction

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Correction.newCorrection {
            withCompileScript("echo \"compilation skipped\"")

            addOutputTask {
                withLaunchScript("ls ")
                withHint("sample test")
                expectStdoutResult("hello world")
                giveMarkOnSuccess(10)
                setTimeoutMinutes(2)
            }

            addOutputTask {
                withLaunchScript("./sample.sh")
                withHint("sample test should fail")
                expectStdoutResult("unexpected result")
                giveMarkOnSuccess(10)
                setTimeoutMinutes(10)
            }
        }
    }
}