package com.redpills.correction.framework.dto

data class WorkerInnerTestResultDTO(
        val purpose: String,
        val children: List<WorkerTestResultDTO>,
        val state: WorkerTestResultStateDTO? = null,
        val mark: Int
)

data class WorkerTestResultDTO(
        val containerId: ContainerIdentityDTO,
        val purpose: String,
        var mark: String,
        val children: MutableList<WorkerInnerTestResultDTO>
)