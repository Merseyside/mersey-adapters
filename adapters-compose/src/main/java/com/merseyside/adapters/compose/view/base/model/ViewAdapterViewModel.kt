package com.merseyside.adapters.compose.view.base.model

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel

typealias ViewVM<View> = AdapterParentViewModel<View, SCV>
typealias NestedViewVM<View> = NestedAdapterParentViewModel<View, SCV, SCV>