package com.merseyside.adapters.core.model.update

import com.merseyside.adapters.core.model.AdapterParentViewModel

suspend fun <Model> Model.update(update: suspend ModelUpdater<Model>.(model: Model) -> Unit)
    where Model : AdapterParentViewModel<*, *>, Model : UpdatableModel<Model> {
    modelUpdater.update { model -> update(modelUpdater, model) }
}