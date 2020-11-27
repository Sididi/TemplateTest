package com.redpills.correction.framework

import com.redpills.correction.framework.domain.Correction

class Main {
    companion object {
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
}