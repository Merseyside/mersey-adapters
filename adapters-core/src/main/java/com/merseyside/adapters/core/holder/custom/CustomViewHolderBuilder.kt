package com.merseyside.adapters.core.holder.custom

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.ViewHolderBuilder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.utils.view.ext.getActivity

class CustomViewHolderBuilder<V: View, Item : Parent, Parent, Model> :
    ViewHolderBuilder<Parent, Model>()
    where Model : AdapterParentViewModel<Item, Parent> {

    lateinit var createView: (context: Context, viewType: Int) -> V
    lateinit var bind: (view: V, model: Model) -> Unit

    override fun build(parent: ViewGroup, viewType: Int): ViewHolder<Parent, Model> {
        val view = createView(parent.getActivity(), viewType)
        return CustomViewHolder(view, bind)
    }
}