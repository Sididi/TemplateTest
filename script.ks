Correction.newCorrection {
    withCompileScript("echo \"compilation skipped\"")

    addOutputTask {
        withLaunchScript("chmod 777 find_iv && ./find_iv clair encrypte key")
        withHint("Test avec IV simple")
        expectStdoutResult("\"Z3JIZ29pcmVncmVnb2lyZQ==\"")
        giveMarkOnSuccess(10)
        setTimeoutMinutes(1)
    }
}