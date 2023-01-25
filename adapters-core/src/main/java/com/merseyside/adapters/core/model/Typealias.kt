package com.merseyside.adapters.core.model


typealias VM<Parent> = AdapterParentViewModel<out Parent, Parent>

typealias AdapterViewModel<Item> = AdapterParentViewModel<Item, Item>
typealias NestedAdapterViewModel<Item, Data> = NestedAdapterParentViewModel<Item, Item, Data>