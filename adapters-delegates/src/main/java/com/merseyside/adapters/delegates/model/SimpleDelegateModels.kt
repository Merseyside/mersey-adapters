package com.merseyside.adapters.delegates.model

import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.ExpandableAdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel

typealias SimpleAdapterViewModel<Item> = AdapterParentViewModel<Item, Any>
typealias SimpleNestedAdapterViewModel<Item, Data> = NestedAdapterParentViewModel<Item, Any, Data>
typealias SimpleExpandableAdapterViewModel<Item, Data> = ExpandableAdapterParentViewModel<Item, Any, Data>