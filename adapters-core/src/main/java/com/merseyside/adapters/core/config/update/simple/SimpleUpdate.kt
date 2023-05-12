package com.merseyside.adapters.core.config.update.simple

import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.update.UpdateRequest

class SimpleUpdate<Parent, Model : VM<Parent>>(
    override var updateActions: UpdateActions<Parent, Model>
) : UpdateLogic<Parent, Model> {

    override suspend fun update(
        updateRequest: UpdateRequest<Parent>,
        models: List<Model>
    ): Boolean = updateActions.transaction {
            var isUpdated = false

            with(updateRequest) {

                val modelsToRemove = findOutdatedModels(list, models)
                isUpdated = updateActions.removeModels(modelsToRemove)

                list.forEachIndexed { newPosition, item ->
                    val oldModel = models.find { it.areItemsTheSameInternal(item) }
                    if (oldModel == null) {
                        updateActions.add(newPosition, listOf(item))
                    } else {
                        val oldPosition = getPositionOfItem(item, models)
                        updateActions.move(oldModel, oldPosition, newPosition)
                        if (!oldModel.areContentsTheSame(item)) {
                            isUpdated = updateActions.updateModel(oldModel, item) || isUpdated
                        }
                    }
                }

                isUpdated
            }
    }

    override suspend fun update(dest: List<Model>, source: List<Model>) = updateActions.transaction {
        val modelsToRemove = findOutdatedModels(dest.map { it.item }, source)
        updateActions.removeModels(modelsToRemove)

        dest.forEachIndexed { newPosition, model ->
            val oldModel = source.find { it.areItemsTheSameInternal(model.item) }
            if (oldModel == null) {
                updateActions.addModel(newPosition, model)
            } else {
                val oldPosition = getPositionOfModel(model, source)
                updateActions.move(oldModel, oldPosition, newPosition)
                //if (updateActions.updateModel(oldModel, item)) isUpdated = true
            }
        }
    }
}