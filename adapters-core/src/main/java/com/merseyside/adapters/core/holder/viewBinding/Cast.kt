package com.merseyside.adapters.core.holder.viewBinding

import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM

fun <Parent, Model : VM<Parent>> ViewHolder<Parent, Model>.asBindingHolder(): ViewBindingHolder<Parent, Model> {
    if (this is ViewBindingHolder<Parent, Model>) return this
    else throw ClassCastException("Try to cast to ViewBindingHolder failed!")
}