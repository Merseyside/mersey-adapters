package com.merseyside.adapters.core.model.update

import com.merseyside.adapters.core.model.AdapterParentViewModel

interface UpdatableModel<Model : AdapterParentViewModel<*, *>> {

    val modelUpdater: ModelUpdater<Model>
}

class ModelUpdater<Model : AdapterParentViewModel<*, *>>(private val model: Model) {

    private val updatePayloads: MutableList<AdapterParentViewModel.Payloadable> = mutableListOf()
    private var callbacks: MutableSet<ModelUpdaterCallback<Model>> = mutableSetOf()

    suspend fun update(update: suspend (model: Model) -> Unit) {
        update(model)
        endUpdate()
    }

    fun addPayload(payload: AdapterParentViewModel.Payloadable) {
        updatePayloads.add(payload)
    }

    fun addCallback(callback: ModelUpdaterCallback<Model>) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: ModelUpdaterCallback<Model>) {
        callbacks.remove(callback)
    }

    private suspend fun endUpdate() {
        val payloads = updatePayloads.toList()
        updatePayloads.clear()
        notifyUpdated(payloads)
    }

    private suspend fun notifyUpdated(payloads: List<AdapterParentViewModel.Payloadable>) {
        model.onUpdate()
        callbacks.forEach { callback -> callback.onUpdate(model, payloads) }
    }

    fun interface ModelUpdaterCallback<Model : AdapterParentViewModel<*, *>> {
        suspend fun onUpdate(model: Model, payloads: List<AdapterParentViewModel.Payloadable>)
    }
}