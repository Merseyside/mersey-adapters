package com.merseyside.adapters.core.holder.binding

import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> ViewHolder<Parent, Model>.asBindingHolder()
        : BindingViewHolder<Parent, Model> {
    if (this is BindingViewHolder<Parent, Model>) return this
    else throw ClassCastException("Try to cast to BindingViewHolder failed!")
}

inline fun <reified ViewBinding> ViewHolder<*, *>.requireViewBinding(): ViewBinding {
    val bindingHolder = asBindingHolder()
    return bindingHolder.binding as ViewBinding
}