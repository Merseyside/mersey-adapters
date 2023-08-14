package com.merseyside.adapters.core.listManager

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.NestedAdapterActions


interface INestedModelListManager<Parent, Model, InnerData, InnerAdapter> :
    IModelListManager<Parent, Model>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out VM<InnerData>> {

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
        return adapterActions.removeNestedAdapterByModel(model)
    }

    override suspend fun updateModel(model: Model, item: Parent): Boolean {
        return super.updateModel(model, item).also {
            val adapter = provideNestedAdapter(model)
            model.getNestedData()?.let { data ->
                workManager.subTaskWith(adapter) {
                    update(data)
                }
            }
        }
    }

    override suspend fun createModel(item: Parent): Model? {
        return super.createModel(item)?.also { model ->
            val adapter = provideNestedAdapter(model)
            model.getNestedData()?.let { data ->
                workManager.subTaskWith(adapter) {
                    add(data)
                }
            }
        }
    }
}