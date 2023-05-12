@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.core.base

import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.base.callback.OnItemClickListener
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.ext.hasFeature
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.utils.safeLet
import com.merseyside.utils.reflection.ReflectionUtils

abstract class BaseAdapter<Parent, Model>(
    override val adapterConfig: AdapterConfig<Parent, Model>,
) : RecyclerView.Adapter<TypedBindingHolder<Model>>(),
    HasOnItemClickListener<Parent>, IBaseAdapter<Parent, Model>, ILogger
        where Model : VM<Parent> {

    override val models: List<Model>
        get() = listManager.modelList

    override lateinit var workManager: AdapterWorkManager

    @InternalAdaptersApi
    override val adapter: RecyclerView.Adapter<TypedBindingHolder<Model>>
        get() = this

    protected var isRecyclable: Boolean = true

    private var onAttachToRecyclerViewListeners: MutableList<OnAttachToRecyclerViewListener> =
        mutableListOf()

    @InternalAdaptersApi
    override var clickListeners: MutableList<OnItemClickListener<Parent>> = ArrayList()

    @InternalAdaptersApi
    val bindItemList: MutableList<Model> = ArrayList()
    protected var recyclerView: RecyclerView? = null

    override val provideModelByItem: suspend (Parent) -> Model = { item ->
        createModel(item).also { model ->
            onModelCreated(model)
        }
    }

    fun addOnAttachToRecyclerViewListener(listener: OnAttachToRecyclerViewListener) {
        onAttachToRecyclerViewListeners.add(listener)
        safeLet(recyclerView) { listener.onAttached(it, this) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getModelClass(): Class<Model> {
        return ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            BaseAdapter::class.java,
            1
        ) as Class<Model>
    }

    @InternalAdaptersApi
    abstract fun createModel(item: Parent): Model

    override val callbackClick: (Parent) -> Unit = { item ->
        clickListeners.forEach { listener -> listener.onItemClicked(item) }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        super.onAttachedToRecyclerView(recyclerView)
        onAttachToRecyclerViewListeners.forEach { it.onAttached(recyclerView, this) }
    }

    override fun getItemCount(): Int {
        return listManager.getItemCount()
    }

    override fun onBindViewHolder(holder: TypedBindingHolder<Model>, position: Int) {
        val model = getModelByPosition(position)
        bindModel(holder, model, position)

        bindItemList.add(model)
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(
        holder: TypedBindingHolder<Model>,
        position: Int,
        payloads: List<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val payloadable = payloads.first() as List<AdapterParentViewModel.Payloadable>
            if (isPayloadsValid(payloadable)) {
                onPayloadable(holder, payloadable)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    @InternalAdaptersApi
    @CallSuper
    open fun bindModel(
        holder: TypedBindingHolder<Model>,
        model: Model,
        position: Int
    ) {
    }

    open fun removeListeners() {
        removeAllClickListeners()
    }

    override fun hasFeature(key: String): Boolean {
        return adapterConfig.hasFeature(key)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        onAttachToRecyclerViewListeners.forEach { it.onDetached(recyclerView, this) }
        this.recyclerView = null
    }

    override val tag: String = "BaseAdapter"
}