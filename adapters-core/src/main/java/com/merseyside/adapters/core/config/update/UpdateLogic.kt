package com.merseyside.adapters.core.config.update

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.merseyLib.kotlin.extensions.subtractBy

interface UpdateLogic<Parent, Model : VM<Parent>> {

    var updateActions: UpdateActions<Parent, Model>

    suspend fun update(updateRequest: UpdateRequest<Parent>, models: List<Model>): Boolean

    suspend fun update(dest: List<Model>, source: List<Model>)

    suspend fun findOutdatedModels(
        newItems: List<Parent>,
        models: List<Model>,
    ): List<Model> {
        return if (newItems.isEmpty()) {
            models.filter { model -> model.isDeletable }
        } else {
            models.subtractBy(newItems) { oldModel, newItem ->
                !oldModel.isDeletable || oldModel.areItemsTheSameInternal(newItem)
            }.toList()
        }
    }

//    @Suppress("UNCHECKED_CAST")
//    fun getModelByItem(item: Parent, models: List<Model>): Model? {
//        return if (models is ModelList<*, Model>) {
//            val modelList = models as ModelList<Parent, Model>
//            modelList.findModelByItem(item)
//        } else models.find { model -> model.areItemsTheSameInternal(item) }
//    }

    fun getPositionOfItem(item: Parent, models: List<Model>): Int {
        return models.indexOfFirst { model -> model.areItemsTheSameInternal(item) }
    }
}