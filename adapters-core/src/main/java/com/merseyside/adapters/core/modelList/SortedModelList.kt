package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.VM
import kotlinx.coroutines.runBlocking
import com.merseyside.adapters.core.feature.sorting.Comparator
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.sortedList.SortedList
import com.merseyside.adapters.core.sortedList.find
import com.merseyside.adapters.core.sortedList.removeAll
import com.merseyside.adapters.core.sortedList.subList

class SortedModelList<Parent, Model : VM<Parent>>(
    internal val sortedList: SortedList<Model>,
    private val comparator: Comparator<Parent, Model>
) : ModelList<Parent, Model>() {

    init {
        val callback = object : SortedList.Callback<Model>() {
            override fun onInserted(position: Int, count: Int) {
                val models = getModels().subList(position, position + count)
                onInserted(models, position)
            }

            override fun onRemoved(position: Int, count: Int) {
                onRemoved(emptyList(), position)
            }

            override fun onMoved(fromPosition: Int, toPosition: Int) {
                this@SortedModelList.onMoved(fromPosition, toPosition)
            }

            override fun onChanged(position: Int, count: Int) {}

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

        sortedList.setCallback(callback)
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
        return sortedList.remove(model)
    }

    override suspend fun removeAll(models: List<Model>) {
        sortedList.removeAll(models)
    }

    override fun lastIndexOf(element: Model): Int {
        return indexOf(element)
    }

    override fun indexOf(element: Model): Int = runBlocking {
        sortedList.indexOf(element)
    }

    override suspend fun addAll(models: List<Model>) {
        sortedList.addAll(models)
    }

    override suspend fun add(model: Model) {
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

    override fun clear() {
        sortedList.clear()
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