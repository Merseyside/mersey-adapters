package com.merseyside.adapters.delegates.simple

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.callback.click.OnItemClickListener
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.ViewHolderBuilder
import com.merseyside.adapters.core.holder.binding.BindingViewHolderBuilder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.adapters.delegates.holder.CustomDelegateViewHolderBuilder
import com.merseyside.adapters.delegates.manager.DelegatesManager
import com.merseyside.utils.reflection.ReflectionUtils

abstract class DelegateAdapter<Item : Parent, Parent, Model> : IDelegateAdapter<Item, Parent, Model>
        where Model : AdapterParentViewModel<Item, Parent> {

    private val getLayoutIdInternal: (viewType: Int) -> Int = { getLayoutIdForItem() }

    private var viewHolderBuilder: ViewHolderBuilder<Parent, Model> =
        BindingViewHolderBuilder(getLayoutIdInternal, ::getBindingVariable)

    override var clickListeners: MutableList<OnItemClickListener<Item>> = ArrayList()

    open var getRelativeDelegatesManager: (() -> DelegatesManager<Parent, Model>)? = null

    @Suppress("UNCHECKED_CAST")
    override val adapter: BaseAdapter<Item, *>
        get() = getRelativeCompositeAdapter() as BaseAdapter<Item, *>

    fun getViewType(): Int {
        val manager = getRelativeDelegatesManager?.invoke()
        check(manager != null) {
            "Delegate hasn't been attached to any delegate manager!"
        }

        return manager.getViewTypeByDelegate(this)
    }

    @InternalAdaptersApi
    protected fun requireRelativeDelegatesManager(): DelegatesManager<Parent, Model> {
        return getRelativeDelegatesManager?.invoke()
            ?: throw NullPointerException("Delegate not attached to delegates manager!")
    }

    @OptIn(InternalAdaptersApi::class)
    protected fun getAttachedRecyclerView(): RecyclerView? {
        return requireRelativeDelegatesManager().adapter.recyclerView
    }

    @InternalAdaptersApi
    private val onClick: (Item) -> Unit = { item -> notifyOnClick(item) }

    fun <V : View> setupViewHolder(
        createView: (context: Context, adapter: CompositeAdapter<Parent, Model>) -> V,
        bind: (V, Model) -> Unit
    ) {
        viewHolderBuilder =
            CustomDelegateViewHolderBuilder<V, Item, Parent, Model, CompositeAdapter<Parent, Model>>().apply {
                this.createView = { context, _, adapter -> createView(context, adapter) }
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

    @Suppress("UNCHECKED_CAST")
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
    internal open fun createViewModel(parent: Parent, parentAdapter: CompositeAdapter<Parent, Model>): Model {
        val item = (parent as? Item) ?: throw IllegalArgumentException(
            "This delegate is not responsible for ${parent!!::class}"
        )
        return createItemViewModel(item).also { model -> onModelCreated(model, parentAdapter) }
    }

    /**
     * Delegates has no viewType by itself. That's why pass -1
     */
    @Suppress("UNCHECKED_CAST")
    fun createViewHolder(
        parent: ViewGroup,
        adapter: CompositeAdapter<Parent, Model>
    ): ViewHolder<Parent, Model> {
        if (viewHolderBuilder is CustomDelegateViewHolderBuilder<*, *, *, *, *>) {
            (viewHolderBuilder as CustomDelegateViewHolderBuilder<*, Item, Parent, Model, CompositeAdapter<Parent, Model>>)
                .adapter = adapter
        }
        return viewHolderBuilder.build(parent, -1)
    }

    @CallSuper
    open fun onBindViewHolder(
        holder: ViewHolder<Parent, Model>,
        model: Model,
        position: Int
    ) {
        holder.bind(model)
    }

    open fun onBindViewHolder(
        holder: ViewHolder<Parent, Model>,
        model: Model,
        position: Int,
        payloads: List<Any>
    ) {
    }

    open suspend fun clear() {}

    @OptIn(InternalAdaptersApi::class)
    protected fun getRelativeCompositeAdapter(): CompositeAdapter<Parent, Model> {
        return requireRelativeDelegatesManager().adapter
    }

    @Suppress("UNCHECKED_CAST")
    private val itemClass: Class<Item> by lazy {
        ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            DelegateAdapter::class.java,
            0
        ) as Class<Item>
    }

    @Suppress("UNCHECKED_CAST")
    internal val modelClass: Class<Model> by lazy {
        ReflectionUtils.getGenericParameterClass(
            this.javaClass,
            DelegateAdapter::class.java,
            2
        ) as Class<Model>
    }
}

typealias Delegate<Parent, ParentModel> = DelegateAdapter<out Parent, Parent, ParentModel>