package com.redpills.correction.framework

import com.redpills.correction.framework.domain.Correction

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Correction.newCorrection {
                withCompileScript("echo \"compilation skipped\"")

                addOutputTask {
                    withLaunchScript("chmod 777 sample.sh && ./sample.sh")
                    withHint("sample test")
                    expectStdoutResult("hello world")
                    giveMarkOnSuccess(10)
                    setTimeoutMinutes(2)
                }
            }
        }
    }
}