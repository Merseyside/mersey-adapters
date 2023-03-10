package com.merseyside.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.config
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.model.AdapterViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi

abstract class SimpleAdapter<Item, Model>(
    adapterConfig: AdapterConfig<Item, Model>
): BaseAdapter<Item, Model>(adapterConfig)
    where Model : AdapterViewModel<Item> {
    protected abstract fun getLayoutIdForPosition(position: Int): Int
    protected abstract fun getBindingVariable(): Int
    protected abstract fun createItemViewModel(item: Item): Model

    @InternalAdaptersApi
    override fun bindModel(holder: TypedBindingHolder<Model>, model: Model, position: Int) {
        super.bindModel(holder, model, position)
        holder.bind(getBindingVariable(), model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypedBindingHolder<Model> {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(layoutInflater, viewType, parent, false)

        return getBindingHolder(binding)
    }

    open fun getBindingHolder(binding: ViewDataBinding): TypedBindingHolder<Model> {
        return TypedBindingHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return getLayoutIdForPosition(position)
    }

    @InternalAdaptersApi
    override fun createModel(item: Item): Model = createItemViewModel(item)

    @CallSuper
    @InternalAdaptersApi
    override fun onViewRecycled(holder: TypedBindingHolder<Model>) {
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