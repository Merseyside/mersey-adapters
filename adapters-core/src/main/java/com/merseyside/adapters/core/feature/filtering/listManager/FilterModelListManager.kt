package com.merseyside.adapters.core.feature.filtering.listManager

import com.merseyside.adapters.core.base.AdapterActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.listManager.IModelListManager
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.extensions.move
import com.merseyside.merseyLib.kotlin.logger.ILogger

open class FilterModelListManager<Parent, Model : VM<Parent>>(
    override val modelList: ModelList<Parent, Model>,
    override val adapterActions: AdapterActions<Parent, Model>,
    val adapterFilter: AdapterFilter<Parent, Model>,
    override val workManager: AdapterWorkManager
) : IModelListManager<Parent, Model>, ILogger {

    override lateinit var updateLogic: UpdateLogic<Parent, Model>

    protected var isFiltering: Boolean = false

    protected val isFiltered: Boolean
        get() = adapterFilter.isFiltered

    private val mutAllModelList: MutableList<Model> = ArrayList()
    private val allModelList: List<Model> = mutAllModelList

    private val filteredList: List<Model>
        get() = modelList

    init {
        adapterFilter.apply {
            initListProviders(
                fullListProvider = { allModelList },
                filteredListProvider = { filteredList }
            )

            setFilterCallback { models -> filterUpdate { update(models, filteredList) } }
        }
    }

    override suspend fun addModel(model: Model): Boolean {
        if (!isFiltering) {
            mutAllModelList.add(model)
        }

        return if (adapterFilter.filter(model)) super.addModel(model)
        else false
    }

    override suspend fun removeModel(model: Model): Boolean {
        if (!isFiltering) {
            mutAllModelList.remove(model)
        }

        return super.removeModel(model)
    }

    override suspend fun removeModels(models: List<Model>): Boolean {
        models.forEach { model -> removeModel(model) }
        return true
    }

    override fun findModelByItem(item: Parent): Model? {
        return allModelList.find { model ->
            model.areItemsTheSameInternal(item)
        }
    }

    @InternalAdaptersApi
    override suspend fun clear() {
        mutAllModelList.clear()
        super.clear()
    }

    final override suspend fun update(updateRequest: UpdateRequest<Parent>): Boolean {
        return updateLogic.update(updateRequest, allModelList)
    }

    override suspend fun addModels(models: List<Model>): Boolean {
        if (!isFiltering) {
            mutAllModelList.addAll(models)
        }

        val filteredModels = if (isFiltered) {
            adapterFilter.filter(models)
        } else models

        return super.addModels(filteredModels)
    }

    override suspend fun updateModel(model: Model, item: Parent): Boolean {
        return super.updateModel(model, item).also {

            if (isFiltered) {
                val filtered = adapterFilter.filter(model)
                if (!filtered) removeModel(model)
            }
        }
    }

    protected fun areListsEquals() = filteredList.size == allModelList.size

    override suspend fun addModel(position: Int, model: Model): Boolean {
        requireValidPosition(position, allModelList)

        if (isFiltered) {
            if (adapterFilter.filter(model)) {
                super.addModel(position, model)
            }
        } else {
            mutAllModelList.add(position, model)
            super.addModel(position, model)
        }

        return true
    }

    override suspend fun addModels(position: Int, models: List<Model>): Boolean {
        models.forEachIndexed { index, parent ->
            addModel(position + index, parent)
        }

        return true
    }

    private fun calculatePositionInFilteredList(desiredPosition: Int): Int {
        fun getNearestFilteredPosition(): Int {
            for (index in desiredPosition..allModelList.lastIndex) {
                if (filteredList.contains(allModelList[index])) return index
            }

            for (index in desiredPosition downTo 0) {
                if (filteredList.contains(allModelList[index])) return index
            }

            return 0
        }

        return if (areListsEquals()) {
            desiredPosition
        } else {
            val pos = getNearestFilteredPosition()
            if (pos < desiredPosition) pos + 1
            else pos
        }
    }

    override suspend fun move(oldModel: Model, fromPosition: Int, toPosition: Int) {
        if (isFiltered) {
            mutAllModelList.move(fromPosition, toPosition)
        } else super.move(oldModel, fromPosition, toPosition)
    }

    suspend fun filterUpdate(block: suspend () -> Unit) {
        isFiltering = true
        block()
        isFiltering = false
    }

    override val tag = "FilterFeature"
}

internal typealias Filters = MutableMap<String, Any>