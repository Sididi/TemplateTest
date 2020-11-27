package com.redpills.correction.framework.dto

enum class WorkerTestResultStateDTO {
    PENDING,
    PASSED,
    TIMEOUT,
    FAILED;

    override fun toString(): String {
        return super.name.toUpperCase()
    }
}