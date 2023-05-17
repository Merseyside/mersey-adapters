package com.merseyside.adapters.compose.view.list

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.delegate.NestedViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.simple.ComposingList
import com.merseyside.adapters.compose.view.list.simple.ComposingListStyle
import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroupDelegate
import com.merseyside.adapters.core.base.callback.onClick
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

    override fun getNestedRecyclerView(binding: ViewDataBinding, model: Model): RecyclerView? {
        return (binding.root as RecyclerView).also { recyclerView ->
            with(model.item.listConfig) {
                recyclerView.layoutManager = layoutManager(binding.root.context)
                safeLet(decorator) { recyclerView.addItemDecoration(it) }
            }
        }
    }

    override fun applyStyle(
        context: Context,
        viewDataBinding: ViewDataBinding,
        style: ComposingListStyle
    ) {
        super.applyStyle(context, viewDataBinding, style)
        val recyclerView = viewDataBinding.root as RecyclerView

    }

    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    @InternalAdaptersApi
    override fun createNestedAdapter(
        model: Model
    ): InnerAdapter {
        return super.createNestedAdapter(model).also { adapter ->
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
}