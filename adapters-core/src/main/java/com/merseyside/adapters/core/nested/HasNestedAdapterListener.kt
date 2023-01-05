package com.merseyside.adapters.core.nested

interface HasNestedAdapterListener<InnerData> {

    var onInitAdapterListener: OnInitNestedAdapterListener<InnerData>?

    fun setInitNestedAdapterListener(listener: OnInitNestedAdapterListener<InnerData>) {
        onInitAdapterListener = listener
    }
}