package com.merseyside.adapters.delegates.manager

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.isEmpty
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.set.DelegateAdapterSet
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.composites.CompositeAdapter
import com.merseyside.merseyLib.kotlin.extensions.isNotZero
import com.merseyside.merseyLib.kotlin.logger.Logger
import com.merseyside.utils.ext.*

open class DelegatesManager<Delegate, Parent, ParentModel>(
    delegates: List<DelegateAdapter<out Parent, Parent, out ParentModel>> = emptyList()
) where ParentModel : VM<Parent>,
        Delegate : DelegateAdapter<out Parent, Parent, ParentModel> {

    protected val delegates = SparseArray<Delegate>()
    private lateinit var onDelegateRemoveCallback: suspend (DelegateAdapter<out Parent, Parent, *>) -> Unit

    internal lateinit var getRelativeAdapter: () -> CompositeAdapter<Parent, ParentModel>

    protected val count: Int
        get() = delegates.size()

    init {
        addDelegateList(delegates)
    }

    private fun addDelegateListInternal(delegates: List<Delegate>) {
        val size = count
        delegates.forEachIndexed { index, delegateAdapter ->
            addDelegate(delegateAdapter, size + index)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun addDelegateList(delegates: List<DelegateAdapter<out Parent, Parent, out ParentModel>>) {
        addDelegateListInternal(delegates as List<Delegate>)
    }

    @Suppress("UNCHECKED_CAST")
    fun addDelegates(vararg delegates: DelegateAdapter<out Parent, Parent, out ParentModel>) {
        addDelegateList(delegates.toList() as List<Delegate>)
    }

    fun addDelegateSet(delegateSet: DelegateAdapterSet<*, out Parent, Parent, out ParentModel>) {
        val list = delegateSet.getDelegates()
        list.forEachIndexed { index, delegateAdapter ->
            delegates.put(count + index, delegateAdapter as Delegate)
        }
    }

    @OptIn(InternalAdaptersApi::class)
    private fun addDelegate(delegate: Delegate, key: Int = count) {
        if (!delegates.containsKey(key)) {
            delegates.put(key, delegate)

            if (delegate.getRelativeDelegatesManager != null)
                Logger.logErr("DelegatesManager", "Warning: $delegate already attached" +
                        " to another manager. Reatached to another manager.")

            delegate.getRelativeDelegatesManager = { this }

        } else throw IllegalArgumentException("View type already exists!")
    }

    fun createViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Parent, ParentModel> {
        return getDelegateByViewType(viewType).createViewHolder(parent, viewType)
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
        requireDelegate { getResponsibleDelegate(model) }.onBindViewHolder(
            holder,
            model,
            position,
            payloads
        )
    }

    fun getViewTypeByItem(model: ParentModel): Int {
        return if (count.isNotZero()) {
            delegates.findKey { it.second.isResponsibleForParent(model.item) }
                ?: throw IllegalArgumentException("No responsible delegates found!")
        } else throw IllegalStateException("Delegates are empty. Please, add delegates before using this!")
    }

    fun getDelegateByViewType(viewType: Int): Delegate {
        return requireDelegate { delegates.get(viewType) }
    }

    fun getDelegateKey(delegate: Delegate): Int {
        val index = delegates.indexOfValue(delegate)
        return if (index >= 0) {
            delegates.keyAt(index)
        } else throw IllegalArgumentException("View type of passed delegate not found!")
    }

    fun hasDelegate(delegate: Delegate): Boolean {
        return delegates.findValue { it.second == delegate } != null
    }

    fun hasResponsibleDelegate(clazz: Class<out Parent>): Boolean {
        return getResponsibleDelegate(clazz) != null
    }

    fun hasResponsibleDelegate(item: Parent): Boolean {
        return getResponsibleDelegate(item) != null
    }

    fun getResponsibleDelegate(clazz: Class<out Parent>): Delegate? {
        return delegates.findValue { it.second.isResponsibleForItemClass(clazz) }
    }

    open fun getResponsibleDelegate(item: Parent): Delegate? {
        return delegates.findValue { it.second.isResponsibleForParent(item) }
    }

    open fun getResponsibleDelegates(item: Parent): List<Delegate> {
        return delegates.filterValues { it.isResponsibleForParent(item) }
    }

    suspend fun removeResponsibleDelegate(clazz: Class<out Parent>): Boolean {
        val delegate = getResponsibleDelegate(clazz)
        return delegate?.let {
            onDelegateRemoveCallback(delegate)
            val key = getDelegateKey(delegate)
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
        delegates.forEach {
            it.second.clear()
        }
    }

    fun isEmpty(): Boolean = delegates.isEmpty()

    internal open fun getResponsibleDelegate(model: ParentModel): Delegate {
        return if (count.isNotZero()) {
            requireDelegate { delegates.findValue { it.second.isResponsibleForParent(model.item) } }
        } else throw IllegalStateException("Delegates are empty. Please, add delegates call this method!")
    }

    internal fun createModel(item: Parent): ParentModel {
        val delegate = requireDelegate {
            delegates.findValue {
                it.second.isResponsibleForParent(item)
            } ?: run {
                Logger.logErr("Can not find delegate for ${item!!::class}")
                null
            }
        }
        return delegate.createViewModel(item)
    }

    internal fun setOnDelegateRemoveCallback(callback: suspend (DelegateAdapter<out Parent, Parent, *>) -> Unit) {
        onDelegateRemoveCallback = callback
    }

    private fun requireDelegate(
        block: () -> Delegate?
    ): Delegate {
        return block() ?: throw NullPointerException("Delegate was required but have null!")
    }
}

class SimpleDelegatesManager<Parent, ParentModel> :
    DelegatesManager<DelegateAdapter<out Parent, Parent, ParentModel>, Parent, ParentModel>()
        where ParentModel : VM<Parent>
