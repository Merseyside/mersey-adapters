package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.AdapterParentViewModel


interface ModelListCallback<Model> {

    /**
     * Calls before data inserts in model list
     * Optional
     *
     */
    suspend fun onInsert(models: List<Model>, count: Int = models.size) {}
    suspend fun onInserted(models: List<Model>, position: Int, count: Int = models.size)

    /**
     * Calls before data removes from model list
     * Optional
     */
    suspend fun onRemove(models: List<Model>, count: Int = models.size) {}
    suspend fun onRemoved(models: List<Model>, position: Int, count: Int = models.size)

    suspend fun onChanged(model: Model, position: Int, payloads: List<AdapterParentViewModel.Payloadable>)

    suspend fun onMoved(fromPosition: Int, toPosition: Int)

    suspend fun onCleared()
}