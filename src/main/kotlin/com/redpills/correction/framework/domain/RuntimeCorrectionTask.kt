package com.redpills.correction.framework.domain

abstract class RuntimeCorrectionTask(
    var hint: String,
    var mark: Int
): RuntimeTask