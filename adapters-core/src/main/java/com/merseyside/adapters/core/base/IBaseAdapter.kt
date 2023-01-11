@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.core.base

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.feature.positioning.PositionFeature
import com.merseyside.adapters.core.holder.TypedBindingHolder
import com.merseyside.adapters.core.listManager.IModelListManager
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelListCallback
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.merseyLib.kotlin.extensions.isZero
import kotlin.math.max
import kotlin.math.min

@SuppressLint("NotifyDataSetChanged")
interface IBaseAdapter<Parent, Model> : AdapterActions<Parent, Model>,
    HasOnItemClickListener<Parent>, ModelListCallback<Model>, HasAdapterWorkManager
        where Model : VM<Parent> {

    override var workManager: AdapterWorkManager
    val adapterConfig: AdapterConfig<Parent, Model>
    val models: List<Model>

    @InternalAdaptersApi
    val listManager: IModelListManager<Parent, Model>
        get() = adapterConfig.listManager

    val adapter: RecyclerView.Adapter<TypedBindingHolder<Model>>

    @InternalAdaptersApi
    val callbackClick: (Parent) -> Unit

    @CallSuper
    override suspend fun onInserted(models: List<Model>, position: Int, count: Int) = runForUI {
        if (count == 1) {
            adapter.notifyItemInserted(position)
        } else {
            adapter.notifyItemRangeInserted(position, count)
        }

        notifyPositionsChanged(position)
    }

    @CallSuper
    override suspend fun onRemoved(models: List<Model>, position: Int, count: Int) = runForUI {
        if (count == 1) {
            adapter.notifyItemRemoved(position)
        } else {
            adapter.notifyItemRangeRemoved(position, count)
        }

        notifyPositionsChanged(position)
    }

    override suspend fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) = runForUI {
        adapter.notifyItemChanged(position, payloads)
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) = runForUI {
        adapter.notifyItemMoved(fromPosition, toPosition)
        notifyPositionsChanged(toPosition, fromPosition)
    }

    suspend fun add(item: Parent): Model? {
        return listManager.add(item)
    }

    /**
     * Delegates items adding to [IModelListManager]
     * @return Added models
     */
    suspend fun add(items: List<Parent>) {
        listManager.add(items)
    }

    suspend fun addOrUpdate(items: List<Parent>) {
        if (listManager.getItemCount().isZero()) {
            add(items)
        } else {
            update(items)
        }
    }

    suspend fun update(updateRequest: UpdateRequest<Parent>): Boolean {
        return listManager.update(updateRequest)
    }

    suspend fun update(items: List<Parent>): Boolean {
        return listManager.update(UpdateRequest(items))
    }

    @InternalAdaptersApi
    @CallSuper
    suspend fun onModelCreated(model: Model) {
        model.clickEvent.observe(observer = callbackClick)
    }

    /**
     * Removes model by item
     * Calls onItemRemoved callback method on success.
     * @return position of removed item
     */
    suspend fun remove(item: Parent): Model? {
        return listManager.remove(item)
    }


    suspend fun remove(items: List<Parent>): List<Model> {
        return listManager.remove(items)
    }

    fun notifyAdapterRemoved() {}

    fun isPayloadsValid(payloads: List<AdapterParentViewModel.Payloadable>): Boolean {
        return payloads.isNotEmpty() &&
                !payloads.contains(AdapterParentViewModel.Payloadable.None)
    }

    fun onPayloadable(
        holder: TypedBindingHolder<Model>,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
    }

    fun getItemCount(): Int

    fun getLastPositionIndex(): Int = getItemCount() - 1

    fun getItemByPosition(position: Int): Parent {
        return getModelByPosition(position).item
    }

    fun getModelByPosition(position: Int): Model {
        return listManager.getModelByPosition(position)
    }

    suspend fun getModelByItem(item: Parent): Model? {
        return listManager.getModelByItem(item)
    }

    suspend fun getPositionOfItem(item: Parent): Int {
        val model = getModelByItem(item)
        return if (model != null) {
            getPositionOfModel(model)
        } else -1
    }

    suspend fun containsModel(model: Model): Boolean {
        return models.contains(model)
    }

    fun find(item: Parent): Model? {
        return models.find { model ->
            model.areItemsTheSameInternal(item)
        }
    }


    suspend fun clear() {
        listManager.clear()
    }

    /**
     * @return true if modelList has items else - false
     */
    fun isEmpty(): Boolean = getItemCount().isZero()

    fun isNotEmpty(): Boolean = !isEmpty()

    @Throws(IndexOutOfBoundsException::class)
    fun first(): Parent {
        try {
            return getModelByPosition(0).item
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    @Throws(IndexOutOfBoundsException::class)
    fun last(): Parent {
        try {
            return getModelByPosition(getLastPositionIndex()).item
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    fun getAll(): List<Parent> {
        return models.map { model -> model.item }
    }

    suspend fun removeAll() {
        listManager.clear()
        adapter.notifyDataSetChanged()
    }

    suspend fun getPositionOfModel(model: Model): Int {
        return listManager.getPositionOfModel(model)
    }

    /* Position */


    suspend fun add(position: Int, item: Parent) {
        listManager.add(position, item)
    }


    suspend fun add(position: Int, items: List<Parent>) {
        listManager.add(position, items)
    }

    suspend fun addBefore(beforeItem: Parent, item: Parent) {
        val position = getPositionOfItem(beforeItem)
        add(position, item)
    }

    suspend fun addBefore(beforeItem: Parent, items: List<Parent>) {
        val position = getPositionOfItem(beforeItem)
        add(position, items)
    }

    suspend fun addAfter(afterItem: Parent, item: Parent) {
        val position = getPositionOfItem(afterItem)
        add(position + 1, item)
    }

    suspend fun addAfter(afterItem: Parent, item: List<Parent>) {
        val position = getPositionOfItem(afterItem)
        add(position + 1, item)
    }

    fun notifyPositionsChanged(newPosition: Int, oldPosition: Int = -1) {
        if (hasFeature(PositionFeature.key)) {
            val range = calculateChangedPositionsRange(newPosition, oldPosition)
            for (index in range) {
                models[index].onPositionChanged(index)
            }
        }
    }

    fun calculateChangedPositionsRange(newPosition: Int, oldPosition: Int = -1): IntRange {
        return if (oldPosition == -1) {
            newPosition..models.lastIndex
        } else {
            min(oldPosition, newPosition)..max(oldPosition, newPosition)
        }
    }

    fun hasFeature(key: String): Boolean

    @Throws(IllegalStateException::class)
    fun getModelClass(): Class<Model>
}