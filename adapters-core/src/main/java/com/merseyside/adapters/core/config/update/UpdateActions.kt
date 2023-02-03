package com.merseyside.adapters.core.config.update

import com.merseyside.adapters.core.model.VM


interface UpdateActions<Parent, Model : VM<Parent>> {
    suspend fun <R> transaction(block: suspend UpdateActions<Parent, Model>.() -> R): R

    suspend fun add(items: List<Parent>): List<Model>

    suspend fun add(position: Int, items: List<Parent>): List<Model>

    suspend fun addModel(model: Model): Boolean

    suspend fun addModel(position: Int, model: Model): Boolean

    suspend fun removeModels(models: List<Model>): Boolean

    suspend fun updateModels(pairs: List<Pair<Model, Parent>>)

    suspend fun updateModel(model: Model, item: Parent): Boolean

    suspend fun move(oldModel: Model, fromPosition: Int, toPosition: Int)
}