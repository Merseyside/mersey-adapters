package com.merseyside.adapters.delegates.manager

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.isEmpty
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.Delegate
import com.merseyside.adapters.delegates.set.DelegateAdapterSet
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.merseyLib.kotlin.extensions.isNotZero
import com.merseyside.utils.ext.*

open class DelegatesManager<Parent, ParentModel>(
    delegates: List<DelegateAdapter<out Parent, Parent, ParentModel>> = emptyList()
) where ParentModel : VM<Parent> {

    protected val delegates = SparseArray<Delegate<Parent, ParentModel>>()
    private lateinit var onDelegateRemoveCallback: suspend (DelegateAdapter<out Parent, Parent, *>) -> Unit

    lateinit var adapter: CompositeAdapter<Parent, ParentModel>
        internal set

    protected val count: Int
        get() = delegates.size()

    init {
        addDelegateList(delegates)
    }

    private fun addDelegateListInternal(delegates: List<Delegate<Parent, ParentModel>>) {
        val size = count
        delegates.forEachIndexed { index, delegateAdapter ->
            addDelegate(delegateAdapter, size + index)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun addDelegateList(delegates: List<Delegate<Parent, out ParentModel>>) {
        addDelegateListInternal(delegates as List<Delegate<Parent, ParentModel>>)
    }

    fun addDelegates(vararg delegates: Delegate<Parent, out ParentModel>) {
        addDelegateList(delegates.toList())
    }

    @Suppress("UNCHECKED_CAST")
    fun addDelegateSet(delegateSet: DelegateAdapterSet<*, out Parent, Parent, out ParentModel>) {
        val list = delegateSet.getDelegates()
        list.forEachIndexed { index, delegateAdapter ->
            delegates.put(count + index, delegateAdapter as Delegate<Parent, ParentModel>)
        }
    }

    private fun addDelegate(delegate: Delegate<Parent, ParentModel>, key: Int = count) {
        if (!delegates.containsKey(key)) {
            delegates.put(key, delegate)

//            if (delegate.getRelativeDelegatesManager != null)
//                Logger.logErr(
//                    "DelegatesManager", "Warning: $delegate already attached" +
//                            " to another manager. Reatached to another manager."
//                )

            delegate.getRelativeDelegatesManager = {
                this
            }

        } else throw IllegalArgumentException("View type already exists!")
    }

    fun createViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Parent, ParentModel> {
        return getDelegateByViewType(viewType).createViewHolder(parent, adapter)
    }

    internal fun onBindViewHolder(
        holder: ViewHolder<Parent, ParentModel>,
        model: ParentModel,
        position: Int
    ) {
        requireDelegate { getResponsibleDelegate(model) }.onBindViewHolder(holder, model, position)
    }

    internal fun onBindViewHolder(
        holder: ViewHolder<Parent, ParentModel>,
        position: Int,
        payloads: List<Any>
    ) {
        val model = holder.model
        requireDelegate { getResponsibleDelegate(model) }
            .onBindViewHolder(holder, model, position, payloads)
    }

    internal fun getViewTypeByDelegate(delegate: Delegate<Parent, ParentModel>): Int {
        return delegates.findKey { (_, d) -> delegate == d }
            ?: throw NullPointerException("View type of passed delegate not found!")
    }

    internal fun getViewTypeByItem(model: ParentModel): Int {
        return if (count.isNotZero()) {
            delegates.findKey { (_, delegate) -> delegate.isResponsibleForParent(model.item) }
                ?: throw IllegalArgumentException("No responsible delegates found!")
        } else throw IllegalStateException("Delegates are empty. Please, add delegates before using this!")
    }

    fun getDelegateByViewType(viewType: Int): Delegate<Parent, ParentModel> {
        return requireDelegate { delegates.get(viewType) }
    }

    fun getResponsibleDelegate(clazz: Class<out Parent>): Delegate<Parent, ParentModel>? {
        return delegates.findValue { it.second.isResponsibleForItemClass(clazz) }
    }

    open fun getResponsibleDelegate(item: Parent): Delegate<Parent, ParentModel>? {
        return delegates.findValue { it.second.isResponsibleForParent(item) }
    }

    open fun getResponsibleDelegates(item: Parent): List<Delegate<Parent, ParentModel>> {
        return delegates.filterValues { it.isResponsibleForParent(item) }
    }

    suspend fun removeResponsibleDelegate(clazz: Class<out Parent>): Boolean {
        val delegate = getResponsibleDelegate(clazz)
        return delegate?.let {
            onDelegateRemoveCallback(delegate)
            val key = getViewTypeByDelegate(delegate)
            delegates.remove(key)
            true
        } ?: false
    }

    suspend fun removeResponsibleDelegate(item: Parent): Boolean {
        return removeResponsibleDelegate(item!!::class.java)
    }

    fun clear() {
        delegates.clear()
    }

    suspend fun resetDelegates() {
        delegates.forEach { it.second.clear() }
    }

    fun isEmpty(): Boolean = delegates.isEmpty()

    internal open fun getResponsibleDelegate(model: ParentModel): Delegate<Parent, ParentModel> {
        return requireDelegate { getResponsibleDelegate(model.item) }
    }

    internal fun createModel(item: Parent): ParentModel {
        val delegate = requireDelegate { getResponsibleDelegate(item) }
        return delegate.createViewModel(item)
    }

    internal fun setOnDelegateRemoveCallback(
        callback: suspend (DelegateAdapter<out Parent, Parent, *>) -> Unit
    ) {
        onDelegateRemoveCallback = callback
    }

    private fun requireDelegate(
        block: () -> Delegate<Parent, ParentModel>?
    ): Delegate<Parent, ParentModel> {
        return block() ?: throw NullPointerException("Delegate is required but have null!")
    }

    open fun getDelegates(): List<Delegate<Parent, ParentModel>> {
        return delegates.values()
    }

    open fun getChildDelegatesManager(): DelegatesManager<Parent, ParentModel> {
        return DelegatesManager(getDelegates())
    }
}
