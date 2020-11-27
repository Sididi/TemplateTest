package com.redpills.correction.framework.dto

data class WorkerTestResultDTO(
        val containerId: ContainerIdentityDTO,
        val purpose: String,
        val children: MutableList<WorkerTestResultDTO>,
        val state: WorkerTestResultStateDTO? = null
)