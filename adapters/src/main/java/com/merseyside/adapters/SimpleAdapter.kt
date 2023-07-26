package com.merseyside.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.ViewHolderBuilder
import com.merseyside.adapters.core.holder.binding.BindingViewHolderBuilder
import com.merseyside.adapters.core.holder.custom.CustomViewHolderBuilder
import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi

abstract class SimpleAdapter<Item, Model>(adapterConfig: AdapterConfig<Item, Model>) :
    BaseAdapter<Item, Model>(adapterConfig)
        where Model : AdapterViewModel<Item> {

    private var viewHolderBuilder: ViewHolderBuilder<Item, Model> =
        BindingViewHolderBuilder(::getLayoutIdForViewType, ::getBindingVariable)

    @LayoutRes
    protected open fun getLayoutIdForViewType(viewType: Int): Int {
        throw NotImplementedError(
            "This method calls if view holder builder requires view type." +
                    " Please override this method."
        )
    }

    protected open fun getBindingVariable(): Int {
        throw NotImplementedError(
            "This method calls if view holder builder requires view type." +
                    " Please override this method."
        )
    }

    fun <V : View> setupViewHolder(
        createView: (context: Context, viewType: Int) -> V,
        bind: (V, Model) -> Unit
    ) {
        viewHolderBuilder = CustomViewHolderBuilder<V, Item, Item, Model>().apply {
            this.createView = createView
            this.bind = bind
        }
    }

    protected abstract fun createItemViewModel(item: Item): Model

    protected fun setViewHolderBuilder(builder: ViewHolderBuilder<Item, Model>) {
        viewHolderBuilder = builder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item, Model> {
        return viewHolderBuilder.build(parent, viewType)
    }

    @InternalAdaptersApi
    override fun createModel(item: Item): Model = createItemViewModel(item)

    @InternalAdaptersApi
    override fun bindModel(holder: ViewHolder<Item, Model>, model: Model, position: Int) {
        holder.bind(model)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }
}