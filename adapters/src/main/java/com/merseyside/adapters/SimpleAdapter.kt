package com.merseyside.adapters

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.builder.BindingViewHolderBuilder
import com.merseyside.adapters.core.holder.builder.ViewHolderBuilder
import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi

abstract class SimpleAdapter<Item, Model>(
    adapterConfig: AdapterConfig<Item, Model>
): BaseAdapter<Item, Model>(adapterConfig)
    where Model : AdapterViewModel<Item> {

    @LayoutRes
    protected open fun getLayoutIdForPosition(position: Int): Int {
        throw NotImplementedError("This method calls if view holder builder requires view type." +
                " Please override this method.")
    }

    protected open fun getBindingVariable(): Int {
        throw NotImplementedError("This method calls if view holder builder requires view type." +
                " Please override this method.")
    }

    protected abstract fun createItemViewModel(item: Item): Model

    private var viewHolderBuilder: ViewHolderBuilder<Item, Model> = BindingViewHolderBuilder(::getBindingVariable)
    
    protected fun setViewHolderBuilder(builder: ViewHolderBuilder<Item, Model>) {
        viewHolderBuilder = builder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Item, Model> {
        return viewHolderBuilder.build(parent, viewType)
    }

    @InternalAdaptersApi
    override fun bindModel(holder: ViewHolder<Item, Model>, model: Model, position: Int) {
        super.bindModel(holder, model, position)
        holder.bind(model)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    @InternalAdaptersApi
    override fun createModel(item: Item): Model = createItemViewModel(item)

    @CallSuper
    @InternalAdaptersApi
    override fun onViewRecycled(holder: ViewHolder<Item, Model>) {
        super.onViewRecycled(holder)
        if (holder.absoluteAdapterPosition != RecyclerView.NO_POSITION &&
            holder.absoluteAdapterPosition < itemCount) {

            getModelByPosition(holder.absoluteAdapterPosition).apply {
                bindItemList.remove(this)
                onRecycled()
            }
        }
    }

}