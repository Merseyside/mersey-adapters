package com.merseyside.adapters.core.config.update.simple

import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.model.ext.toItems
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.merseyLib.kotlin.extensions.findPosition

class SimpleUpdate<Parent, Model : VM<Parent>>(
    override var updateActions: UpdateActions<Parent, Model>
) : UpdateLogic<Parent, Model> {

    override suspend fun update(
        updateRequest: UpdateRequest<Parent>,
        models: List<Model>
    ): Boolean = updateActions.transaction {
        if (updateRequest.isUpdateExistingOnly()) {
            updateExistingModels(updateRequest.items, models)
        } else if (updateRequest.isFullUpdate()) {
            fullUpdate(updateRequest.items, models)
        } else {
            throw UnsupportedOperationException(
                "Simple update support only full update or update" +
                        " without adding or removing items. Please use simple add/remove operations"
            )
        }
    }

    override suspend fun update(dest: List<Model>, source: List<Model>) =
        updateActions.transaction {
            val destItems = dest.toItems()
            val modelsToRemove = findOutdatedModels(destItems, source)
            removeModels(modelsToRemove)

            dest.forEachIndexed { newPosition, model ->
                val oldPosition = source.findPosition { it.areItemsTheSameInternal(model.item) }
                if (oldPosition == -1) {
                    addModel(newPosition, model)
                } else {
                    val oldModel = source[oldPosition]
                    move(oldModel, oldPosition, newPosition)
                    //if (updateActions.updateModel(oldModel, item)) isUpdated = true
                }
            }
        }

    private suspend fun UpdateActions<Parent, Model>.fullUpdate(
        items: List<Parent>,
        models: List<Model>
    ): Boolean {
        var isUpdated: Boolean

        isUpdated = removeOutdatedModels(items, models)
        var shiftPosition = 0

        items.forEachIndexed { itemPosition, item ->
            val modelInList = models.getOrNull(itemPosition)
            if (modelInList?.implicitPosition == itemPosition) shiftPosition++
            val positionWithShift = itemPosition + shiftPosition

            val oldModel = findModelByItem(item)
            if (oldModel == null) {
                if (positionWithShift == models.size) add(listOf(item))
                else add(positionWithShift, listOf(item))
            } else {
                val oldPosition = getPositionOfItem(item, models)
                move(oldModel, oldPosition, positionWithShift)
                if (!oldModel.areContentsTheSame(item)) {
                    isUpdated = updateModel(oldModel, item) || isUpdated
                }
            }
        }

        return isUpdated
    }

    private suspend fun UpdateActions<Parent, Model>.updateExistingModels(
        items: List<Parent>,
        models: List<Model>
    ): Boolean {
        var isUpdated = false

        items.forEach { item ->
            val oldModel = findModelByItem(item)
            if (oldModel != null) {
                isUpdated = updateModel(oldModel, item) || isUpdated
            }
        }

        return isUpdated
    }

    private suspend fun UpdateActions<Parent, Model>.removeOutdatedModels(
        items: List<Parent>,
        models: List<Model>
    ): Boolean {
        val modelsToRemove = findOutdatedModels(items, models)
        return removeModels(modelsToRemove)
    }
}