package com.merseyside.adapters.compose.view.list

import android.content.Context
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
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
    : ComposingViewGroupDelegate<View, ComposingListStyle, Model, InnerParent, InnerModel, InnerAdapter>()
        where View : ComposingList,
              Model : NestedAdapterParentViewModel<View, SCV, out InnerParent>,
              InnerParent: SCV,
              InnerModel : VM<InnerParent>,
              InnerAdapter : ViewCompositeAdapter<InnerParent, out InnerModel> {

    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_list

    override fun getNestedView(binding: ViewDataBinding, model: Model): RecyclerView? {
        return (binding.root as RecyclerView).also { recyclerView ->
            with(model.item.listConfig) {
                safeLet(layoutManager) { recyclerView.layoutManager = it }
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

    @InternalAdaptersApi
    override fun initNestedAdapter(
        model: Model
    ): InnerAdapter {
        return super.initNestedAdapter(model).also { adapter ->
            with(adapter) {
                onClick { view -> model.item.listConfig.notifyOnClick(view) }
            }
        }
    }
}