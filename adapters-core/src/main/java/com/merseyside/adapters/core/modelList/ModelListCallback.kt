package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.AdapterParentViewModel


interface ModelListCallback<Model> {

    suspend fun onInserted(models: List<Model>, position: Int, count: Int = 1)

    suspend fun onRemoved(models: List<Model>, position: Int, count: Int = 1)

    suspend fun onChanged(model: Model, position: Int, payloads: List<AdapterParentViewModel.Payloadable>)

    suspend fun onMoved(fromPosition: Int, toPosition: Int)

    suspend fun onCleared()
}