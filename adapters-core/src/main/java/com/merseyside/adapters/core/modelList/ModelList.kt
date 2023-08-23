package com.merseyside.adapters.core.modelList

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.callback.ModelListCallback
import com.merseyside.adapters.core.modelList.callback.OnModelListChangedCallback
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.kotlin.logger.ILogger

/**
 * This is a mutable list and mustn't be used as a source of models for adapter.
 * When all mutations completed it calls [OnModelListChangedCallback]'s method onModelListUpdated(newModelList).
 * Adapter handles it and use as a source of models data.
 */
abstract class ModelList<Parent, Model : VM<Parent>>(override val workManager: AdapterWorkManager) :
    List<Model>, HasAdapterWorkManager, ILogger {

    private val callbacks = ArraySet<ModelListCallback<Model>>()
    private val modelListChangedCallbacks = ArraySet<OnModelListChangedCallback>()

    private var isBatched = false

    internal val hasNotAppliedChanges: Boolean
        get() = workManager.mainWorkList.isNotEmpty()

    private val hashMap: MutableMap<Any, Model> = mutableMapOf()

    init {
        workManager.addOnMainWorkStartedListener {
            callbacks.forEach { callback -> callback.onModelListUpdated(getModels()) }
        }
    }

    internal suspend fun <R> batchedUpdate(update: suspend () -> R): R {
        return if (isBatched) update()
        else {
            isBatched = true
            val result = handleListChanges(update)
            isBatched = false

            result
        }
    }

    private suspend fun <R> handleListChanges(update: suspend () -> R): R {
        val oldSize = count()
        val result = update()
        val newSize = count()

        onModelListChanged(oldSize, newSize)

        return result
    }

    private fun postMainWork(work: suspend () -> Unit) {
        workManager.postMainWork(work)
    }

    fun getModelByItem(item: Parent): Model? {
        return try {
            getModelByIdentifiable(item)
        } catch (e: RuntimeException) {
            getModels().find { model ->
                model.areItemsTheSameInternal(item)
            }
        }
    }

    @Throws(RuntimeException::class)
    private fun getModelByIdentifiable(item: Parent): Model? {
        return if (item !is Identifiable<*>) throw RuntimeException("Item is not identifiable!")
        else hashMap[item.id]
    }

    fun addModelListCallback(callback: ModelListCallback<Model>) {
        callbacks.add(callback)
    }

    fun removeModelListCallback(callback: ModelListCallback<Model>) {
        callbacks.remove(callback)
    }

    fun addOnModelListChangedCallback(callback: OnModelListChangedCallback) {
        modelListChangedCallbacks.add(callback)
    }

    fun removeOnModelListChangedCallback(callback: OnModelListChangedCallback) {
        modelListChangedCallbacks.remove(callback)
    }

    @MainThread
    protected suspend fun onInserted(models: List<Model>, position: Int, count: Int = models.size) {
        models.forEach { model -> hashMap[model.id] = model }

        if (!isBatched) onModelListChanged(size - count, size)
        postMainWork { callbacks.forEach { it.onInserted(models, position) } }
    }

    @MainThread
    protected suspend fun onRemoved(
        models: List<Model>,
        position: Int,
        count: Int = models.size
    ) {
        models.map { it.id }.forEach { id -> hashMap.remove(id) }

        if (!isBatched) onModelListChanged(size + count, size)
        postMainWork { callbacks.forEach { it.onRemoved(models, position, count) } }
    }

    @MainThread
    protected suspend fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) = postMainWork {
        callbacks.forEach { it.onChanged(model, position, payloads) }
    }

    @MainThread
    protected suspend fun onMoved(fromPosition: Int, toPosition: Int) = postMainWork {
        callbacks.forEach { it.onMoved(fromPosition, toPosition) }
    }

    @MainThread
    protected suspend fun onCleared(sizeBeforeCleared: Int) {
        if (!isBatched) onModelListChanged(sizeBeforeCleared, size)
        postMainWork { callbacks.forEach { it.onCleared() } }
    }

    private suspend fun onModelListChanged(oldSize: Int, newSize: Int) {
        modelListChangedCallbacks.forEach { callback ->
            callback.onModelListChanged(oldSize, newSize, hasNotAppliedChanges)
        }
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

    protected abstract suspend fun clearAll()

    suspend fun clear() {
        val models = getModels()
        val nonDeletableModels = models.filter { !it.isDeletable }
        if (nonDeletableModels.isNotEmpty()) {
            val deletableModels = models.subtract(nonDeletableModels.toSet())
            removeAll(deletableModels.toList())
        } else clearAll()
    }

    abstract override operator fun get(index: Int): Model

    override fun contains(element: Model): Boolean {
        return getModels().contains(element)
    }

    override fun containsAll(elements: Collection<Model>): Boolean {
        return getModels().containsAll(elements)
    }

    abstract fun getPositionOfModel(model: Model): Int

    protected abstract fun getModelByItemInternal(item: Parent): Model?

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