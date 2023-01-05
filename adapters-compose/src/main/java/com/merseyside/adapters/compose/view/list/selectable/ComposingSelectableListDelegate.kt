package com.merseyside.adapters.compose.view.list.selectable

import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.manager.ViewDelegatesManager
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.BaseComposingListDelegate
import com.merseyside.adapters.core.feature.selecting.Selecting

class ComposingSelectableListDelegate : BaseComposingListDelegate<ComposingSelectableList,
        ComposingSelectableListViewModel, SCV, ViewAdapterViewModel,
        ViewCompositeAdapter<SCV, ViewAdapterViewModel>>() {

    override fun createItemViewModel(item: ComposingSelectableList) = ComposingSelectableListViewModel(item)


    override fun createCompositeAdapter(
        model: ComposingSelectableListViewModel,
        delegateManager: ViewDelegatesManager<SCV, ViewAdapterViewModel>
    ): ViewCompositeAdapter<SCV, ViewAdapterViewModel> {
        return ViewCompositeAdapter(delegateManager) {
            apply(model.item.listConfig.adapterConfig)

            Selecting {
                with(model.item) {
                    selectableMode = listConfig.selectableMode
                    isSelectEnabled = listConfig.isSelectEnabled
                    isAllowToCancelSelection = listConfig.isAllowToCancelSelection

                    onSelect = { item, isSelected, isSelectedByUser ->
                        model.item.listConfig.notifyOnSelected(item, isSelected, isSelectedByUser)
                    }
                }
            }
        }
    }
}