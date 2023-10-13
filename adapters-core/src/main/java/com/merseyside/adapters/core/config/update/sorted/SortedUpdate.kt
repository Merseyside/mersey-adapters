package com.merseyside.adapters.core.config.update.sorted

import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.merseyLib.kotlin.extensions.subtractBy
import com.merseyside.merseyLib.kotlin.logger.log
import com.merseyside.merseyLib.kotlin.logger.logSimpleTag

class SortedUpdate<Parent, Model : VM<Parent>>(
    override var updateActions: UpdateActions<Parent, Model>
) : UpdateLogic<Parent, Model> {

    override suspend fun update(
        updateRequest: UpdateRequest<Parent>,
        models: List<Model>
    ): Boolean {
        val updateTransaction = getUpdateTransaction(updateRequest, models)
        return applyUpdateTransaction(updateTransaction)
    }

    private suspend fun getUpdateTransaction(
        updateRequest: UpdateRequest<Parent>,
        models: List<Model>
    ): UpdateTransaction<Parent, Model> {
        val updateTransaction = UpdateTransaction<Parent, Model>()
        with(updateTransaction) {
            if (updateRequest.removeOld) {
                modelsToRemove = findOutdatedModels(updateRequest.items, models)
            }

            val addList = ArrayList<Parent>()
            val updateList = ArrayList<Pair<Model, Parent>>()

            updateRequest.items.forEach { newItem ->
                val model = updateActions.findModelByItem(newItem)
                if (model == null) {
                    if (updateRequest.addNew) {
                        addList.add(newItem)
                    }
                } else {
                    if (!model.areContentsTheSame(newItem)) {
                        updateList.add(model to newItem)
                    }
                }
            }

            modelsToUpdate = updateList
            itemsToAdd = addList
        }

        return updateTransaction
    }

    private suspend fun applyUpdateTransaction(
        updateTransaction: UpdateTransaction<Parent, Model>
    ): Boolean = updateActions.transaction {
        with(updateTransaction) {
            if (modelsToRemove.isNotEmpty()) {
                removeModels(modelsToRemove)
            }

            if (modelsToUpdate.isNotEmpty()) {
                updateModels(modelsToUpdate)
            }

            if (itemsToAdd.isNotEmpty()) {
                add(itemsToAdd)
            }

            !updateTransaction.isEmpty()
        }
    }

    override suspend fun update(dest: List<Model>, source: List<Model>) =
        updateActions.transaction {
            val modelsToRemove = findOutdatedModels(dest.map { it.item }, source)
            updateActions.removeModels(modelsToRemove)


            val addModels = dest.subtractBy(source) { sourceModel, destItem ->
                sourceModel.areItemsTheSameInternal(destItem.item)
            }
            addModels.forEach { updateActions.addModel(it) }
        }
}