package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator
import com.merseyside.adapters.core.feature.sorting.sortedList.SortedList
import com.merseyside.adapters.core.feature.sorting.sortedList.find
import com.merseyside.adapters.core.feature.sorting.sortedList.removeAll
import com.merseyside.adapters.core.feature.sorting.sortedList.subList
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.workManager.AdapterWorkManager

class SortedModelList<Parent, Model : VM<Parent>>(
    workManager: AdapterWorkManager,
    private val sortedList: SortedList<Model>,
    private val comparator: Comparator<Parent, Model>
) : ModelList<Parent, Model>(workManager) {

    private val sortedListCallback = object : SortedList.Callback<Model>() {
        override suspend fun onInserted(position: Int, count: Int) {
            val models = getModels().subList(position, position + count)
            onInserted(models, position)
        }

        /**
         * Must be called with single item
         */
        override suspend fun onRemoved(position: Int, count: Int, model: Model) {
            if (count > 1) throw IllegalArgumentException()
            onRemoved(listOf(model), position, count)
        }

        override suspend fun onMoved(fromPosition: Int, toPosition: Int) {
            this@SortedModelList.onMoved(fromPosition, toPosition)
        }

        override suspend fun onChanged(position: Int, count: Int) {}

        override fun compare(item1: Model, item2: Model): Int {
            return if (item1 != item2) comparator.compare(item1, item2)
            else 0
        }

        override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
            return oldItem.areContentsTheSame(newItem.item)
        }

        override fun areItemsTheSame(item1: Model, item2: Model): Boolean {
            return item1.areItemsTheSameInternal(item2.item)
        }
    }

    init {
        sortedList.setCallback(sortedListCallback)
    }

    override suspend fun onUpdated(
        model: Model,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
        val oldPosition = getPositionOfModel(model)
        val newPosition = sortedList.recalculatePositionOfItemAt(oldPosition)
        if (oldPosition == newPosition) {
            onChanged(model, oldPosition, payloads)
        }
    }

    override fun getModels(): List<Model> {
        return sortedList.getAll()
    }

    override fun getModelByItemInternal(item: Parent): Model? {
        return sortedList.find { it.areItemsTheSameInternal(item) }
    }

    override fun getPositionOfModel(model: Model): Int {
        return sortedList.indexOf(model)
    }

    override suspend fun add(model: Model) {
        sortedList.add(model)
    }

    override suspend fun addAll(models: List<Model>) {
        batchedUpdate { sortedList.addAll(models) }
    }

    override fun get(index: Int): Model {
        return sortedList[index]
    }

    override fun iterator(): Iterator<Model> {
        return listIterator()
    }

    override suspend fun remove(model: Model): Boolean {
        return sortedList.remove(model)
    }

    override suspend fun removeAll(models: List<Model>) {
        sortedList.removeAll(models)
    }

    override suspend fun addAll(position: Int, models: List<Model>) {
        throw Exception("Adding by position is not supported.")
    }

    override suspend fun add(position: Int, model: Model) {
        throw Exception("Adding by position is not supported.")
    }

    override fun lastIndexOf(element: Model): Int {
        return indexOf(element)
    }

    override fun indexOf(element: Model): Int {
        return sortedList.indexOf(element)
    }

    override suspend fun updateModel(
        model: Model,
        newItem: Parent
    ) {
        val oldPosition = getPositionOfModel(model)
        if (oldPosition != -1) {
            val payloads = model.payload(newItem)
            val newPosition = sortedList.recalculatePositionOfItemAt(oldPosition)
            if (oldPosition != newPosition) onChanged(model, newPosition, payloads)
        } else throw NullPointerException("Item not found!")
    }

    override suspend fun clearAll() {
        val sizeBeforeCleared = count()
        doWithoutCallback {
            sortedList.clear()
        }
        onCleared(sizeBeforeCleared)
    }

    suspend fun recalculateItemPositions() {
        batchedUpdate {
            val models = getModels()
            clearAll()
            addAll(models)
        }
    }

    override fun listIterator(): ListIterator<Model> {
        return listIterator(0)
    }

    override fun listIterator(index: Int): ListIterator<Model> {
        return SortedListIterator(index, sortedList)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Model> {
        return sortedList.subList(fromIndex, toIndex)
    }

    private suspend fun doWithoutCallback(block: suspend () -> Unit) {
        sortedList.removeCallback()
        block()
        sortedList.setCallback(sortedListCallback)
    }
}

internal class SortedListIterator<Model>(
    startIndex: Int = 0,
    private val sortedList: SortedList<Model>
) : ModelListIterator<Model>(startIndex) {
    override fun getItem(index: Int): Model {
        return sortedList[index]
    }

    override fun getSize(): Int {
        return sortedList.size()
    }
}