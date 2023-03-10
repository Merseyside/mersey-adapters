package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.logger.ILogger

abstract class ModelList<Parent, Model : VM<Parent>> : List<Model>, ILogger {

    private val callbacks: MutableList<ModelListCallback<Model>> = ArrayList()

    fun addModelListCallback(callback: ModelListCallback<Model>) {
        callbacks.add(callback)
    }

    fun onInserted(models: List<Model>, position: Int) {
        val count = models.size
        callbacks.forEach { it.onInserted(models, position, count) }
    }

    fun onRemoved(models: List<Model>, position: Int) {
        val count = models.size
        callbacks.forEach { it.onRemoved(models, position, count) }
    }

    fun onChanged(model: Model, position: Int, payloads: List<AdapterParentViewModel.Payloadable>) {
        callbacks.forEach { it.onChanged(model, position, payloads) }
    }

    fun onMoved(fromPosition: Int, toPosition: Int) {
        callbacks.forEach { it.onMoved(fromPosition, toPosition) }
    }

    abstract fun getModels(): List<Model>

    val lastIndex: Int
        get() = getModels().lastIndex

    override val size: Int
        get() = getModels().size

    override fun isEmpty(): Boolean = getModels().isEmpty()

    abstract suspend fun add(model: Model)

    abstract suspend fun add(position: Int, model: Model)

    abstract suspend fun addAll(models: List<Model>)

    abstract suspend fun addAll(position: Int, models: List<Model>)

    abstract suspend fun remove(model: Model): Boolean

    abstract suspend fun removeAll(models: List<Model>)

    abstract suspend fun onModelUpdated(model: Model, payloads: List<AdapterParentViewModel.Payloadable>)

    abstract fun clear()

    abstract override operator fun get(index: Int): Model

    override fun contains(element: Model): Boolean {
        return getModels().contains(element)
    }

    override fun containsAll(elements: Collection<Model>): Boolean {
        return getModels().containsAll(elements)
    }

    fun getPositionOfModel(model: Model): Int {
        return getModels().indexOf(model)
    }

    abstract fun getModelByItem(item: Parent): Model?

    override val tag: String = "ModelList"
}

internal abstract class ModelListIterator<Model>(startIndex: Int = 0) : ListIterator<Model> {
    protected var index = startIndex

    abstract fun getItem(index: Int): Model
    abstract fun getSize(): Int

    final override fun hasNext(): Boolean = index < getSize()

    final override fun hasPrevious(): Boolean {
        return index > 0
    }

    final override fun next(): Model {
        if (!hasNext()) throw NoSuchElementException()
        return getItem(index++)
    }

    final override fun nextIndex(): Int {
        return index + 1
    }

    final override fun previous(): Model {
        if (!hasPrevious()) throw NoSuchElementException()
        return getItem(index--)
    }

    final override fun previousIndex(): Int {
        return index - 1
    }
}