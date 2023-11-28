package com.merseyside.adapters.core.holder.binding

import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM

class BindingViewHolder<Parent, Model : VM<Parent>>(
    val binding: ViewDataBinding,
    private val variableId: () -> Int
) : ViewHolder<Parent, Model>(binding.root) {

    override fun bind(model: Model) {
        this.model = model
        binding.setVariable(variableId(), model)
        binding.executePendingBindings()
    }
}