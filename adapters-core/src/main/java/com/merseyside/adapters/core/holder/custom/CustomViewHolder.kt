package com.merseyside.adapters.core.holder.custom

import android.view.View
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel

class CustomViewHolder<V: View, Item : Parent, Parent, Model>(
    override val root: V,
    private val bindModel: (V, Model) -> Unit
) : ViewHolder<Parent, Model>(root)
    where Model : AdapterParentViewModel<Item, Parent> {
    override fun bind(model: Model) {
        this.model = model
        bindModel(root, model)
    }
}