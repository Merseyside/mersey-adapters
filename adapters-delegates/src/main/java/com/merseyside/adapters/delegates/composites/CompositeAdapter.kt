package com.merseyside.adapters.delegates.composites

import android.view.ViewGroup
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.DA
import com.merseyside.adapters.delegates.manager.DelegatesManager

open class CompositeAdapter<Parent, ParentModel>(
    adapterConfig: AdapterConfig<Parent, ParentModel>,
    delegatesManager: DelegatesManager<DA<Parent, ParentModel>, Parent, ParentModel> = DelegatesManager()
) : BaseAdapter<Parent, ParentModel>(adapterConfig)
        where ParentModel : VM<Parent> {

    open val delegatesManager: DelegatesManager<DA<Parent, ParentModel>, Parent, ParentModel> =
        delegatesManager

    init {
        delegatesManager.setOnDelegateRemoveCallback { delegate ->
            val removeList = models.filter { delegate.isResponsibleForParent(it.item) }
            remove(removeList.map { it.item })
        }

        delegatesManager.getRelativeAdapter = { this }
    }

    override fun getItemViewType(position: Int): Int {
        return delegatesManager.getViewTypeByItem(getModelByPosition(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<Parent, ParentModel> {
        return delegatesManager.createViewHolder(parent, viewType)
    }

    @InternalAdaptersApi
    override fun bindModel(
        holder: ViewHolder<Parent, ParentModel>,
        model: ParentModel,
        position: Int
    ) {
        delegatesManager.onBindViewHolder(holder, model, position)
    }

    override fun onBindViewHolder(
        holder: ViewHolder<Parent, ParentModel>,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        delegatesManager.onBindViewHolder(holder, position, payloads)
    }

    @InternalAdaptersApi
    override fun createModel(item: Parent): ParentModel {
        return delegatesManager.createModel(item)
    }

}