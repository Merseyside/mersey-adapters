package com.merseyside.adapters.compose.delegate

import com.merseyside.adapters.compose.adapter.SimpleViewCompositeAdapter
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.manager.ViewDelegatesManager
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.NestedAdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.nestedDelegate.INestedDelegateAdapter

abstract class NestedViewDelegateAdapter<View, Style, Model, InnerParent, InnerModel, InnerAdapter> :
    ViewDelegateAdapter<View, Style, Model>(),
    INestedDelegateAdapter<View, SCV, Model, InnerParent, InnerAdapter>
        where View : StyleableComposingView<Style>,
              InnerParent : SCV,
              Style : ComposingStyle,
              Model : NestedAdapterParentViewModel<View, SCV, out InnerParent>,
              InnerModel : AdapterParentViewModel<out InnerParent, InnerParent>,
              InnerAdapter : ViewCompositeAdapter<InnerParent, out InnerModel> {

    override var adapterList: MutableList<Pair<Model, InnerAdapter>> = ArrayList()

    abstract fun createCompositeAdapter(
        model: Model,
        delegateManager: ViewDelegatesManager<InnerParent, InnerModel>,
    ): InnerAdapter

    @Suppress("UNCHECKED_CAST")
    override fun createNestedAdapter(
        model: Model,
        parentAdapter: CompositeAdapter<SCV, Model>
    ): InnerAdapter {
        val parentManager = (parentAdapter as SimpleViewCompositeAdapter).delegatesManager
        val childManager = parentManager.getChildDelegatesManager()
        return createCompositeAdapter(model, childManager as ViewDelegatesManager<InnerParent, InnerModel>)
    }

    @InternalAdaptersApi
    override fun onBindViewHolder(holder: ViewHolder<SCV, Model>, model: Model, position: Int) {
        super.onBindViewHolder(holder, model, position)
        onBindNestedAdapter(holder, model, position)
    }

    @InternalAdaptersApi
    override fun onBindViewHolder(
        holder: ViewHolder<SCV, Model>,
        model: Model,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, model, position, payloads)
        onModelUpdated(model)
    }

    override suspend fun clear() {
        super.clear()
        clearAdapters()
    }
}