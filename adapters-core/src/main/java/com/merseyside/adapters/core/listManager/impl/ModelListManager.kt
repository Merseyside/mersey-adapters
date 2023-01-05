package com.merseyside.adapters.core.listManager.impl

import com.merseyside.adapters.core.base.AdapterActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.listManager.IModelListManager
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.workManager.AdapterWorkManager

open class ModelListManager<Parent, Model>(
    override val modelList: ModelList<Parent, Model>,
    override val adapterActions: AdapterActions<Parent, Model>,
    override val workManager: AdapterWorkManager
) : IModelListManager<Parent, Model>
        where Model : VM<Parent> {

    override val hashMap: MutableMap<Any, Model> = mutableMapOf()

    override lateinit var updateLogic: UpdateLogic<Parent, Model>

    override suspend fun removeModels(models: List<Model>): Boolean {
        modelList.removeAll(models)
        return true
    }
}