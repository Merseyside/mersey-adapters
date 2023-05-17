package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.coroutines.utils.uiDispatcher
import com.merseyside.merseyLib.kotlin.logger.ILogger
import kotlinx.coroutines.withContext

abstract class ModelList<Parent, Model : VM<Parent>> : List<Model>, ILogger {

    private val batchedUpdates: MutableList<suspend () -> Unit> = ArrayList()

    var isBatched = false
        private set

    suspend fun <R> batchedUpdate(block: suspend () -> R): R {
        isBatched = true
        val result = block()
        isBatched = false

        runForUI {
            batchedUpdates.forEach { update -> update() }
            batchedUpdates.clear()
        }

        return result
    }

    private suspend fun doUpdate(update: suspend () -> Unit) {
        if (isBatched) batchedUpdates.add(update)
        else withContext(uiDispatcher) { update() }
    }

    private val callbacks: MutableList<ModelListCallback<Model>> = ArrayList()

    fun addModelListCallback(callback: ModelListCallback<Model>) {
        callbacks.add(callback)
    }

    fun removeModelListCallback(callback: ModelListCallback<Model>) {
        callbacks.remove(callback)
    }

    protected suspend fun onInsert(models: List<Model>) {
        callbacks.forEach { it.onInsert(models)}
    }

    protected suspend fun onInserted(models: List<Model>, position: Int) = doUpdate {
        callbacks.forEach { it.onInserted(models, position) }
    }

    protected suspend fun onRemove(
        models: List<Model>,
        count: Int = models.size
    )  {
        callbacks.forEach { it.onRemove(models, count) }
    }

    protected suspend fun onRemoved(
        models: List<Model>,
        position: Int,
        count: Int = models.size
    ) = doUpdate {
        callbacks.forEach { it.onRemoved(models, position, count) }
    }

    protected suspend fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) = doUpdate {
        callbacks.forEach { it.onChanged(model, position, payloads) }
    }

    protected suspend fun onMoved(fromPosition: Int, toPosition: Int) = doUpdate {
        callbacks.forEach { it.onMoved(fromPosition, toPosition) }
    }

    protected suspend fun onCleared() = doUpdate {
        callbacks.forEach { it.onCleared() }
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

    abstract suspend fun onModelUpdated(
        model: Model,
        payloads: List<AdapterParentViewModel.Payloadable>
    )

    abstract suspend fun clear()

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