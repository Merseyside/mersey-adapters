package com.merseyside.adapters.compose.view.list.paging

import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.manager.ViewDelegatesManager
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.BaseComposingListDelegate
import com.merseyside.adapters.compose.view.list.simple.ComposingListViewModel
import com.merseyside.adapters.core.async.updateAsync

class ComposingPagingListDelegate :
    BaseComposingListDelegate<ComposingPagingList<*>, ComposingListViewModel<ComposingPagingList<*>>,
            SCV, ViewAdapterViewModel, ViewCompositeAdapter<SCV, ViewAdapterViewModel>>() {

    override fun createCompositeAdapter(
        model: ComposingListViewModel<ComposingPagingList<*>>,
        delegateManager: ViewDelegatesManager<SCV, ViewAdapterViewModel>
    ) = ViewCompositeAdapter(delegateManager, model.item.listConfig.adapterConfig)

    override fun createItemViewModel(item: ComposingPagingList<*>) = ComposingListViewModel(item)
}