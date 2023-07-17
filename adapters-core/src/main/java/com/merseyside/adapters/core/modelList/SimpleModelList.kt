package com.merseyside.adapters.core.modelList

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.extensions.move

open class SimpleModelList<Parent, Model : VM<Parent>>(
    workManager: AdapterWorkManager,
    private val mutModels: MutableList<Model> = ArrayList()
) : ModelList<Parent, Model>(workManager) {

    override fun getModels(): List<Model> {
        return mutModels
    }

    override suspend fun onModelUpdated(
        model: Model,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
        val position = getPositionOfModel(model)
        onChanged(model, position, payloads)
    }

    override fun get(index: Int): Model {
        return mutModels[index]
    }

    override fun getModelByItem(item: Parent): Model? {
        return mutModels.find { it.areItemsTheSameInternal(item) }
    }

    override fun iterator(): Iterator<Model> {
        return listIterator()
    }

    override suspend fun remove(model: Model): Boolean {
        val position = getPositionOfModel(model)
        return try {
            onRemove(listOf(model))
            val removedModel = mutModels.removeAt(position)
            val list = listOf(removedModel)

            onRemoved(list, position)
            true
        } catch (e: IndexOutOfBoundsException) {
            false
        }
    }

    override suspend fun removeAll(models: List<Model>) {
        models.forEach { model -> remove(model) }
    }

    override fun lastIndexOf(element: Model): Int {
        return indexOf(element)
    }

    override fun indexOf(element: Model): Int {
        return mutModels.indexOf(element)
    }

    override suspend fun addAll(models: List<Model>) {
        onInsert(models)
        mutModels.addAll(models)
        onInserted(models, size - models.size)
    }

    override suspend fun add(model: Model) {
        val list = listOf(model)
        onInsert(list)
        mutModels.add(model)
        onInserted(list, lastIndex)
    }

    override suspend fun addAll(position: Int, models: List<Model>) {
        onInsert(models)
        mutModels.addAll(position, models)
        onInserted(models, position)
    }

    override suspend fun add(position: Int, model: Model) {
        val list = listOf(model)
        onInsert(list)
        mutModels.add(position, model)
        onInserted(list, position)
    }

    suspend fun move(fromIndex: Int, toIndex: Int) {
        mutModels.move(fromIndex, toIndex)
        onMoved(fromIndex, toIndex)
    }

    override suspend fun clear() {
        mutModels.clear()
        onCleared()
    }

    override fun listIterator(): ListIterator<Model> {
        return listIterator(0)
    }

    override fun listIterator(index: Int): ListIterator<Model> {
        return SimpleModelListIterator(index, mutModels)
    }

    override fun subList(fromIndex: Int, toIndex: Int): List<Model> {
        return mutModels.subList(fromIndex, toIndex)
    }

    internal class SimpleModelListIterator<Model>(
        startIndex: Int,
        private val list: List<Model>
    ) : ModelListIterator<Model>(startIndex) {
        override fun getItem(index: Int): Model {
            return list[index]
        }

        override fun getSize(): Int {
            return list.size
        }

    }

    override val tag: String = "SimpleModelList"
}