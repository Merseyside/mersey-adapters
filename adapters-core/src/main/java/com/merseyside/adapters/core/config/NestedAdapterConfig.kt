package com.merseyside.adapters.core.config

import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.config.ext.getFeatureByKey
import com.merseyside.adapters.core.config.ext.hasFeature
import com.merseyside.adapters.core.feature.filtering.FilterFeature
import com.merseyside.adapters.core.feature.filtering.listManager.FilterNestedModelListManager
import com.merseyside.adapters.core.listManager.INestedModelListManager
import com.merseyside.adapters.core.listManager.impl.NestedModelModelListManager
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.nested.INestedAdapter

class NestedAdapterConfig<Parent, Model, Data, InnerAdapter> internal constructor() :
    AdapterConfig<Parent, Model>()
        where Model : NestedAdapterParentViewModel<out Parent, Parent, Data>,
              InnerAdapter : BaseAdapter<Data, out VM<Data>> {

    private lateinit var _listManager: INestedModelListManager<Parent, Model, Data, InnerAdapter>

    override val listManager: INestedModelListManager<Parent, Model, Data, InnerAdapter>
        get() = _listManager

    @Suppress("UNCHECKED_CAST")
    override fun initModelListManager(
        adapter: IBaseAdapter<Parent, Model>
    ): INestedModelListManager<Parent, Model, Data, InnerAdapter> {
        adapter as INestedAdapter<Parent, Model, Data, InnerAdapter>
        if (!this::_listManager.isInitialized) {
            _listManager = if (hasFeature(FilterFeature.key)) {
                val filterFeature =
                    getFeatureByKey(FilterFeature.key) as FilterFeature<Parent, Model>
                FilterNestedModelListManager(
                    modelList = initModelList(adapter),
                    adapterActions = adapter,
                    adapterFilter = filterFeature.adapterFilter,
                    workManager = workManager
                )
            } else {
                NestedModelModelListManager(
                    initModelList(adapter),
                    adapter,
                    workManager = workManager
                )
            }.also { initWithUpdateLogic(it) }
        }

        return _listManager
    }
}