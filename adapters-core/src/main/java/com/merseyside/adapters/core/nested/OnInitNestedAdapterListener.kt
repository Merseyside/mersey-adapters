package com.merseyside.adapters.core.nested

import com.merseyside.adapters.core.base.BaseAdapter


interface OnInitNestedAdapterListener<Data> {
    fun onInitNestedAdapter(adapter: BaseAdapter<Data, *>)
}