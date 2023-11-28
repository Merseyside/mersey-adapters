package com.merseyside.adapters.delegates.holder

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.ViewHolderBuilder
import com.merseyside.adapters.core.holder.custom.CustomViewHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.utils.view.ext.getActivity

class CustomDelegateViewHolderBuilder<V: View, Item : Parent, Parent, Model, CompositeAdapter>
    : ViewHolderBuilder<Parent, Model>()
        where Model : AdapterParentViewModel<Item, Parent> {

    lateinit var createView: (context: Context, viewType: Int, adapter: CompositeAdapter) -> V
    lateinit var bind: (view: V, model: Model) -> Unit

    var adapter: CompositeAdapter? = null

    override fun build(parent: ViewGroup, viewType: Int): ViewHolder<Parent, Model> {
        val view = createView(parent.getActivity(), viewType, requireNotNull(adapter))
        return CustomViewHolder(view, bind)
    }
}