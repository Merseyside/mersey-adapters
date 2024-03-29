@file:OptIn(InternalAdaptersApi::class)

package com.merseyside.adapters.core.base

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.base.callback.click.HasOnItemClickListener
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.feature.positioning.PositionFeature
import com.merseyside.adapters.core.listManager.IModelListManager
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.callback.ModelListCallback
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.extensions.isZero
import kotlin.math.max
import kotlin.math.min

interface IBaseAdapter<Parent, Model> : AdapterActions<Parent, Model>,
    HasOnItemClickListener<Parent>, ModelListCallback<Model>, HasAdapterWorkManager
        where Model : VM<Parent> {

    override var workManager: AdapterWorkManager
    val adapterConfig: AdapterConfig<Parent, Model>
    var models: List<Model>

    override val adapter: BaseAdapter<Parent, *>

    val isAttached: Boolean

    @InternalAdaptersApi
    val listManager: IModelListManager<Parent, Model>
        get() = adapterConfig.listManager

    @InternalAdaptersApi
    val callbackClick: (Parent) -> Unit

    fun addOnAttachToRecyclerViewListener(listener: OnAttachToRecyclerViewListener)

    @CallSuper
    override suspend fun onInserted(models: List<Model>, position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
        notifyPositionsChanged(position)
    }

    @CallSuper
    override suspend fun onRemoved(models: List<Model>, position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
        notifyPositionsChanged(position)
    }

    override suspend fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
        adapter.notifyItemChanged(position, payloads)
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
        notifyPositionsChanged(toPosition, fromPosition)
    }

    @SuppressLint("NotifyDataSetChanged")
    override suspend fun onCleared() {
        adapter.notifyDataSetChanged()
    }

    override suspend fun onModelListUpdated(newModelList: List<Model>) {
        models = newModelList
    }

    suspend fun add(item: Parent): Model? {
        return listManager.add(item)
    }

    /**
     * Delegates items adding to [IModelListManager]
     * @return true if mew models have been
     */
    suspend fun add(items: List<Parent>): Boolean {
        return listManager.add(items).isNotEmpty()
    }

    @InternalAdaptersApi
    private suspend fun update(updateRequest: UpdateRequest<Parent>): Boolean {
        return if (isEmpty() && updateRequest.addNew) {
            add(updateRequest.items)
            true
        } else {
            listManager.update(updateRequest)
        }
    }

    suspend fun update(
        items: List<Parent>,
        updateBehaviour: UpdateBehaviour = UpdateBehaviour()
    ): Boolean {
        return update(UpdateRequest.fromBehaviour(items, updateBehaviour))
    }

    @InternalAdaptersApi
    @CallSuper
    suspend fun onModelCreated(model: Model) {
        model.addOnClickListener(callbackClick)
    }

    /**
     * Removes model by item
     * Calls onItemRemoved callback method on success.
     * @return removed model
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

    val size: Int
        get() = models.size

    fun getLastPositionIndex(): Int = size - 1

    fun getItemByPosition(position: Int): Parent {
        return getModelByPosition(position).item
    }

    fun getModelByPosition(position: Int): Model {
        return models[position]
    }

    fun getModelByItem(item: Parent): Model? {
        return listManager.findModelByItem(item)
    }

    fun getPositionOfItem(item: Parent): Int {
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
    fun isEmpty(): Boolean = size.isZero()

    fun isNotEmpty(): Boolean = !isEmpty()

    @Throws(IndexOutOfBoundsException::class)
    fun first(): Parent {
        try {
            return getModelByPosition(0).item
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    fun firstOrNull(): Parent? {
        return if (isNotEmpty()) first() else null
    }

    @Throws(IndexOutOfBoundsException::class)
    fun last(): Parent {
        try {
            return getModelByPosition(getLastPositionIndex()).item
        } catch (e: Exception) {
            throw IndexOutOfBoundsException("List is empty")
        }
    }

    fun lastOrNull(): Parent? {
        return if (isNotEmpty()) last() else null
    }

    fun getAll(): List<Parent> {
        return models.map { model -> model.item }
    }

    fun getPositionOfModel(model: Model): Int {
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

    fun setItemsClickableEnabled(enabled: Boolean) {
        models.forEach { model -> model.isClickable = enabled }
    }

    fun hasFeature(key: String): Boolean
}