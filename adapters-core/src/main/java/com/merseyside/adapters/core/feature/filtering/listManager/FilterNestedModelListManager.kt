package com.merseyside.adapters.core.feature.filtering.listManager

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.feature.filtering.AdapterFilter
import com.merseyside.adapters.core.feature.filtering.NestedAdapterFilter
import com.merseyside.adapters.core.feature.filtering.config.getAdapterFilter
import com.merseyside.adapters.core.listManager.INestedModelListManager
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.nested.NestedAdapterActions
import com.merseyside.adapters.core.workManager.AdapterWorkManager

class FilterNestedModelListManager<Parent, Model, InnerData, InnerAdapter>(
    modelList: ModelList<Parent, Model>,
    override val adapterActions: NestedAdapterActions<Parent, Model, InnerData, InnerAdapter>,
    adapterFilter: AdapterFilter<Parent, Model>,
    workManager: AdapterWorkManager
) : FilterModelListManager<Parent, Model>(modelList, adapterActions, adapterFilter, workManager),
    INestedModelListManager<Parent, Model, InnerData, InnerAdapter>
        where Model : NestedAdapterParentViewModel<out Parent, Parent, InnerData>,
              InnerAdapter : BaseAdapter<InnerData, out AdapterParentViewModel<out InnerData, InnerData>> {

    init {
        if (adapterFilter is NestedAdapterFilter<Parent, Model>) {
            adapterFilter.getAdapterFilterByModel = { model ->
                getInnerAdapterFilter(model)
            }
        }
    }

    override suspend fun initNestedAdapterByModel(model: Model): InnerAdapter {
        return super.initNestedAdapterByModel(model).also { adapter ->
            if (adapterFilter is NestedAdapterFilter<Parent, Model>) {
                adapter.adapterConfig.getAdapterFilter()?.let { innerAdapterFilter ->
                    adapterFilter.initAdapterFilter(innerAdapterFilter)
                }
            }
        }
    }

    private suspend fun getInnerAdapterFilter(model: Model): AdapterFilter<InnerData, *>? {
        val innerAdapter = provideNestedAdapter(model)
        return innerAdapter.adapterConfig.getAdapterFilter()
    }

    override suspend fun updateModel(model: Model, item: Parent): Boolean {
        return super<INestedModelListManager>.updateModel(model, item).also {
            if (isFiltered) {
                val filtered = adapterFilter.filter(model)
                if (!filtered) {
                    removeModel(model)
                }
            }
        }
    }
}