package com.merseyside.adapters.core.holder.builder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.viewBinding.ViewBindingHolder
import com.merseyside.adapters.core.model.VM

class BindingViewHolderBuilder<Parent, Model : VM<Parent>>(private val variableId: () -> Int) :
    ViewHolderBuilder<Parent, Model>() {

    override fun build(parent: ViewGroup, viewType: Int): ViewHolder<Parent, Model> {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, viewType, parent, false)

        return ViewBindingHolder(binding, variableId)
    }

}