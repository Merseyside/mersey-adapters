package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.feature.sorting.comparator.Comparator
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.feature.sorting.sortedList.SortedList
import com.merseyside.adapters.core.feature.sorting.sortedList.find
import com.merseyside.adapters.core.feature.sorting.sortedList.removeAll
import com.merseyside.adapters.core.feature.sorting.sortedList.subList

class SortedModelList<Parent, Model : VM<Parent>>(
    internal val sortedList: SortedList<Model>,
    private val comparator: Comparator<Parent, Model>
) : ModelList<Parent, Model>() {

    private val sortedListCallback = object : SortedList.Callback<Model>() {
        override suspend fun onInserted(position: Int, count: Int) {
            val models = getModels().subList(position, position + count)
            onInserted(models, position)
        }

        override suspend fun onRemoved(position: Int, count: Int) {
            onRemoved(emptyList(), position, count)
        }

        override suspend fun onMoved(fromPosition: Int, toPosition: Int) {
            this@SortedModelList.onMoved(fromPosition, toPosition)
        }

        override suspend fun onChanged(position: Int, count: Int) {}

        override fun compare(item1: Model, item2: Model): Int {
            return comparator.compare(item1, item2)
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

    override fun getModels(): List<Model> {
        return sortedList.getAll()
    }

    override fun getModelByItem(item: Parent): Model? {
        return sortedList.find { it.areItemsTheSameInternal(item) }
    }

    override suspend fun addAll(position: Int, models: List<Model>) {
        throw Exception("Adding by position is not supported.")
    }

    override suspend fun add(position: Int, model: Model) {
        throw Exception("Adding by position is not supported.")
    }

    override fun get(index: Int): Model {
        return sortedList[index]
    }

    override fun iterator(): Iterator<Model> {
        return listIterator()
    }

    override suspend fun remove(model: Model): Boolean {
        onRemove(listOf(model))
        return sortedList.remove(model)
    }

    override suspend fun removeAll(models: List<Model>) {
        onRemove(models)
        sortedList.removeAll(models)
    }

    override fun lastIndexOf(element: Model): Int {
        return indexOf(element)
    }

    override fun indexOf(element: Model): Int {
        return sortedList.indexOf(element)
    }

    override suspend fun addAll(models: List<Model>) {
        onInsert(models)
        sortedList.addAll(models)
    }

    override suspend fun add(model: Model) {
        onInsert(listOf(model))
        sortedList.add(model)
    }

    override suspend fun onModelUpdated(
        model: Model,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
        val position = getPositionOfModel(model)
        if (position != -1) {
            sortedList.recalculatePositionOfItemAt(position)
            onChanged(model, getPositionOfModel(model), payloads)
        }
    }

    override suspend fun clear() {
        doWithoutCallback {
            sortedList.clear()
        }
        onCleared()
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