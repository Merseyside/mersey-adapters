package com.merseyside.adapters.core.listManager

import com.merseyside.adapters.core.async.addOrUpdateAsync
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.nested.NestedAdapterActions


interface INestedModelListManager<Parent, Model, InnerData, InnerAdapter> :
    IModelListManager<Parent, Model>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out AdapterParentViewModel<out InnerData, InnerData>> {

    override val adapterActions: NestedAdapterActions<Parent, Model, InnerData, InnerAdapter>

    suspend fun provideNestedAdapter(model: Model): InnerAdapter {
        return adapterActions.getNestedAdapterByModel(model)
            ?: initNestedAdapterByModel(model)
    }

    suspend fun initNestedAdapterByModel(model: Model): InnerAdapter {
        return adapterActions.initNestedAdapterByModel(model)
    }

    override suspend fun remove(item: Parent): Model? {
        return super.remove(item)?.also { model ->
            removeNestedAdapterByModel(model)
        }
    }

    fun removeNestedAdapterByModel(model: Model): Boolean {
        adapterActions.removeNestedAdapterByModel(model)
        return true
    }

    override suspend fun updateModel(model: Model, item: Parent): Boolean {
        return super.updateModel(model, item).also {
            val adapter = provideNestedAdapter(model)
            model.getNestedData()?.let { data ->
                workManager.subTaskWith(adapter) {
                    addOrUpdateAsync(data)
                }
            }
        }
    }

    override suspend fun createModel(item: Parent): Model {
        return super.createModel(item).also { model ->
            val adapter = provideNestedAdapter(model)
            model.getNestedData()?.let { data ->
                workManager.subTaskWith(adapter) {
                    add(data)
                }
            }
        }
    }
}