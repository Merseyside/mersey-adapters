package com.merseyside.adapters.core.listManager

import androidx.annotation.CallSuper
import com.merseyside.adapters.core.base.AdapterActions
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.SimpleModelList
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.kotlin.logger.Logger

interface IModelListManager<Parent, Model> : UpdateActions<Parent, Model>, HasAdapterWorkManager
        where Model : VM<Parent> {

    val adapterActions: AdapterActions<Parent, Model>
    var updateLogic: UpdateLogic<Parent, Model>

    val modelList: ModelList<Parent, Model>



    fun getItemCount(): Int {
        return modelList.size
    }

    fun getPositionOfModel(model: Model): Int {
        return modelList.indexOf(model)
    }

    private suspend fun provideModel(item: Parent): Model {
        return adapterActions.provideModelByItem(item)
    }

    fun getPositionOfItem(item: Parent, models: List<Model> = modelList): Int {
        models.forEachIndexed { index, model ->
            if (model.areItemsTheSameInternal(item)) return index
        }

        throw IllegalArgumentException("No data found")
    }

    fun getModelByItem(item: Parent): Model? {
        return modelList.getModelByItem(item)
    }

//    fun getModelByItem(item: Parent, models: List<Model>): Model? {
//        return getModelByIdentifiable(item) ?: run {
//            models.find { model ->
//                model.areItemsTheSameInternal(item)
//            }
//        }
//    }

//    private fun getModelByIdentifiable(item: Parent): Model? {
//        return if (item is Identifiable<*>) {
//            hashMap[item.id]
//        } else null
//    }

    @CallSuper
    suspend fun createModel(item: Parent): Model? {
        return provideModel(item)
    }

    suspend fun createModels(items: List<Parent>): List<Model> {
        return items.mapNotNull { item -> createModel(item) }
    }

    override suspend fun <R> transaction(block: suspend UpdateActions<Parent, Model>.() -> R): R {
        return modelList.batchedUpdate { block() }
    }

    suspend fun add(item: Parent): Model? {
        return checkModelNotNull(createModel(item)) { model ->
            addModel(model)
            model
        }
    }

    override suspend fun add(items: List<Parent>): List<Model> {
        val models = createModels(items)
        addModels(models)
        return models
    }

    suspend fun add(position: Int, item: Parent): Model? {
        requireValidPosition(position)
        return checkModelNotNull(createModel(item)) { model ->
            addModel(position, model)
            model
        }
    }

    override suspend fun add(position: Int, items: List<Parent>): List<Model> {
        requireValidPosition(position)
        val models = createModels(items)
        addModels(position, models)
        return models
    }

    suspend fun remove(item: Parent): Model? {
        val model = getModelByItem(item)
        return model?.also { removeModel(model) }
    }

    suspend fun update(updateRequest: UpdateRequest<Parent>): Boolean {
        return updateLogic.update(updateRequest, modelList)
    }

    suspend fun remove(items: List<Parent>): List<Model> {
        return checkNotEmpty(items) {
            items.mapNotNull { item -> remove(item) }
        } ?: emptyList()
    }

    suspend fun update(items: List<Parent>): Boolean {
        return update(UpdateRequest(items))
    }

    suspend fun update(dest: List<Model>, source: List<Model> = modelList) {
        updateLogic.update(dest, source)
    }

    /* Models */

    override suspend fun addModel(model: Model): Boolean {
        modelList.add(model)
        return true
    }

    override suspend fun addModel(position: Int, model: Model): Boolean {
        modelList.add(position, model)
        return true
    }

    suspend fun addModels(models: List<Model>): Boolean {
        modelList.addAll(models)
        return true
    }

    suspend fun addModels(position: Int, models: List<Model>): Boolean {
        modelList.addAll(position, models)
        return true
    }

    override suspend fun updateModel(model: Model, item: Parent): Boolean {
        val payloads = model.payload(item)
        modelList.onModelUpdated(model, payloads)
        return true
    }

    override suspend fun updateModels(pairs: List<Pair<Model, Parent>>) {
        pairs.forEach { (model, item) -> updateModel(model, item) }
    }

    suspend fun removeModel(model: Model): Boolean {
        return modelList.remove(model)
    }

    override suspend fun move(oldModel: Model, fromPosition: Int, toPosition: Int) {
        (modelList as SimpleModelList).move(fromPosition, toPosition)
    }

    fun getModelByPosition(position: Int): Model {
        return modelList[position]
    }

    @InternalAdaptersApi
    suspend fun clear() {
        modelList.clear()
    }

    fun requireValidPosition(position: Int, models: List<Model> = modelList) {
        if (models.size < position) throw IllegalArgumentException(
            "Models size is ${models.size} but position is $position."
        )
    }

    suspend fun <R> checkModelNotNull(model: Model?, block: suspend (Model) -> R): R? {
        return if (model != null) block(model)
        else null
    }

    suspend fun <M, R> checkNotEmpty(list: List<M>, block: suspend (list: List<M>) -> R): R? {
        return if (list.isNotEmpty()) block(list)
        else null
    }
}