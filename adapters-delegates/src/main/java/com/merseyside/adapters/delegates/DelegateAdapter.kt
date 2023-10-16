package com.merseyside.adapters.delegates

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.base.callback.OnItemClickListener
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.ViewHolderBuilder
import com.merseyside.adapters.core.holder.binding.BindingViewHolderBuilder
import com.merseyside.adapters.core.holder.custom.CustomViewHolderBuilder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.utils.reflection.ReflectionUtils

abstract class DelegateAdapter<Item : Parent, Parent, Model> : HasOnItemClickListener<Item>
        where Model : AdapterParentViewModel<Item, Parent> {

    private val getLayoutIdInternal: (viewType: Int) -> Int = { getLayoutIdForItem() }

    private var viewHolderBuilder: ViewHolderBuilder<Parent, Model> =
        BindingViewHolderBuilder(getLayoutIdInternal, ::getBindingVariable)

    override var clickListeners: MutableList<OnItemClickListener<Item>> = ArrayList()

    open var getRelativeDelegatesManager: (() -> DelegatesManager<*, Parent, Model>)? = null

    @InternalAdaptersApi
    protected fun requireRelativeDelegatesManager(): DelegatesManager<*, Parent, Model> {
        return getRelativeDelegatesManager?.invoke()
            ?: throw NullPointerException("Delegate not attached to delegates manager!")
    }

    @OptIn(InternalAdaptersApi::class)
    protected fun getAttachedRecyclerView(): RecyclerView? {
        return requireRelativeDelegatesManager().getRelativeAdapter().recyclerView
    }

    @InternalAdaptersApi
    val onClick: (Item) -> Unit = { item ->
        clickListeners.forEach { listener -> listener.onItemClicked(item) }
    }

    fun setViewHolderBuilder(builder: ViewHolderBuilder<Parent, Model>) {
        viewHolderBuilder = builder
    }

    fun <V : View> setupViewHolder(
        createView: (context: Context) -> V,
        bind: (V, Model) -> Unit
    ) {
        viewHolderBuilder = CustomViewHolderBuilder<V, Item, Parent, Model>().apply {
            this.createView = { context, _ -> createView(context) }
            this.bind = bind
        }
    }

    @LayoutRes
    protected open fun getLayoutIdForItem(): Int {
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

    open fun isResponsibleForParent(parent: Parent): Boolean {
        val isResponsible = parent?.let { isResponsibleForItemClass(it::class.java) }
            ?: throw NullPointerException("Parent is null!")

        return if (isResponsible) isResponsibleFor(parent as Item)
        else false
    }

    open fun isResponsibleForItemClass(clazz: Class<out Parent>): Boolean {
        return itemClass == clazz
    }

    open fun isResponsibleFor(item: Item): Boolean {
        return true
    }

    abstract fun createItemViewModel(item: Item): Model

    @Suppress("UNCHECKED_CAST")
    internal open fun createViewModel(parent: Parent): Model {
        val item = (parent as? Item) ?: throw IllegalArgumentException(
            "This delegate is not responsible for ${parent!!::class}"
        )
        return createItemViewModel(item).also { model -> onModelCreated(model) }
    }

    /**
     * Delegates has no viewType by itself. That's why pass -1
     */
    fun createViewHolder(parent: ViewGroup): ViewHolder<Parent, Model> {
        return viewHolderBuilder.build(parent, -1)
    }

    open suspend fun clear() {}

    @OptIn(InternalAdaptersApi::class)
    open fun onModelCreated(model: Model) {
        model.addOnClickListener(onClick)
    }

    @CallSuper
    open fun onBindViewHolder(holder: ViewHolder<Parent, Model>, model: Model, position: Int) {
        holder.bind(model)
    }

    open fun onBindViewHolder(
        holder: ViewHolder<Parent, Model>,
        model: Model,
        position: Int,
        payloads: List<Any>
    ) {
    }

    @OptIn(InternalAdaptersApi::class)
    protected fun getRelativeCompositeAdapter(): CompositeAdapter<Parent, Model> {
        return requireRelativeDelegatesManager().getRelativeAdapter()
    }

    @Suppress("UNCHECKED_CAST")
    private val itemClass: Class<Item> by lazy {
        ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            DelegateAdapter::class.java,
            0
        ) as Class<Item>
    }

    internal val modelClass: Class<Model> by lazy {
        ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            DelegateAdapter::class.java,
            2
        ) as Class<Model>
    }
}

internal typealias DA<Parent, ParentModel> = DelegateAdapter<out Parent, Parent, ParentModel>