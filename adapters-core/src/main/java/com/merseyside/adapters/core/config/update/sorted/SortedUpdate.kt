package com.merseyside.adapters.core.config.update.sorted

import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.adapters.core.async.runWithDefault
import com.merseyside.merseyLib.kotlin.extensions.subtractBy

class SortedUpdate<Parent, Model : VM<Parent>>(
    override var updateActions: UpdateActions<Parent, Model>
) : UpdateLogic<Parent, Model> {

    override suspend fun update(updateRequest: UpdateRequest<Parent>, models: List<Model>): Boolean {
        val updateTransaction = getUpdateTransaction(updateRequest, models)
        return applyUpdateTransaction(updateTransaction)
    }

    private suspend fun getUpdateTransaction(
        updateRequest: UpdateRequest<Parent>,
        models: List<Model>
    ): UpdateTransaction<Parent, Model> = runWithDefault {
        val updateTransaction = UpdateTransaction<Parent, Model>()
        with(updateTransaction) {
            if (updateRequest.isDeleteOld) {
                modelsToRemove = findOutdatedModels(updateRequest.list, models)
            }

            val addList = ArrayList<Parent>()
            val updateList = ArrayList<Pair<Model, Parent>>()

            updateRequest.list.forEach { newItem ->
                val model = getModelByItem(newItem, models)
                if (model == null) {
                    if (updateRequest.isAddNew) addList.add(newItem)
                } else {
                    if (!model.areContentsTheSame(newItem)) {
                        updateList.add(model to newItem)
                    }
                }
            }

            modelsToUpdate = updateList
            itemsToAdd = addList
        }

        updateTransaction
    }

    private suspend fun applyUpdateTransaction(
        updateTransaction: UpdateTransaction<Parent, Model>
    ): Boolean {
        with(updateTransaction) {
            if (modelsToRemove.isNotEmpty()) {
                updateActions.removeModels(modelsToRemove)
            }

            if (modelsToUpdate.isNotEmpty()) {
                updateActions.updateModels(modelsToUpdate)
            }

            if (itemsToAdd.isNotEmpty()) {
                updateActions.add(itemsToAdd)
            }
        }

        return !updateTransaction.isEmpty()
    }

    override suspend fun update(dest: List<Model>, source: List<Model>) {
        val modelsToRemove = findOutdatedModels(dest.map { it.item }, source)
        updateActions.removeModels(modelsToRemove)

        val addModels = dest.subtractBy(source) { sourceModel, destItem -> sourceModel.areItemsTheSameInternal(destItem.item)}
        addModels.forEach { updateActions.addModel(it) }
    }
}