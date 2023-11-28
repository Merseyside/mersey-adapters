package com.merseyside.adapters.core.holder

import android.view.ViewGroup
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM

abstract class ViewHolderBuilder<Parent, Model : VM<Parent>> {

    abstract fun build(parent: ViewGroup, viewType: Int): ViewHolder<Parent, Model>
}