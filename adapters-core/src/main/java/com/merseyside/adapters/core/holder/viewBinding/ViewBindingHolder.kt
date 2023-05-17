package com.merseyside.adapters.core.holder.viewBinding

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM

class ViewBindingHolder<Parent, Model : VM<Parent>>(
    val binding: ViewDataBinding,
    private val variableId: () -> Int
) : ViewHolder<Parent, Model>(binding.root) {

    init {
        binding.lifecycleOwner = itemView.findViewTreeLifecycleOwner()
    }

    override fun bind(model: Model) {
        this.model = model
        binding.setVariable(variableId(), model)
    }
}