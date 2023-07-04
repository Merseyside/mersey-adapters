package com.merseyside.adapters.compose.view.list

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.delegate.NestedViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.simple.ComposingList
import com.merseyside.adapters.compose.view.list.simple.ComposingListStyle
import com.merseyside.adapters.core.base.callback.onClick
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.utils.safeLet

abstract class BaseComposingListDelegate<View, Model, InnerParent, InnerModel, InnerAdapter>
    : NestedViewDelegateAdapter<View, ComposingListStyle, Model, InnerParent, InnerModel, InnerAdapter>()
        where View : ComposingList,
              Model : NestedAdapterParentViewModel<View, SCV, out InnerParent>,
              InnerParent: SCV,
              InnerModel : VM<InnerParent>,
              InnerAdapter : ViewCompositeAdapter<InnerParent, out InnerModel> {

    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_list

    override fun getNestedRecyclerView(holder: ViewHolder<SCV, Model>, model: Model): RecyclerView? {
        return (holder.root as RecyclerView).also { recyclerView ->
            with(model.item.listConfig) {
                recyclerView.layoutManager = layoutManager(holder.context)
                safeLet(decorator) { recyclerView.addItemDecoration(it) }
            }
        }
    }

    override fun applyStyle(
        context: Context,
        holder: ViewHolder<SCV, Model>,
        style: ComposingListStyle
    ) {
        super.applyStyle(context, holder, style)
        val recyclerView = holder.root as RecyclerView

    }

    override fun getBindingVariable() = BR.model

    @OptIn(InternalAdaptersApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun onNestedAdapterCreated(adapter: InnerAdapter, model: Model) {
        with(adapter) {
            model.item
                .listComposeContext
                .setRelativeAdapter(adapter as ViewCompositeAdapter<SCV, VM<SCV>>)

            onClick { view -> model.item.listConfig.notifyOnClick(view) }
            model.item.listConfig.attachToRecyclerViewListeners.forEach {
                adapter.addOnAttachToRecyclerViewListener(it)
            }
        }
    }
}