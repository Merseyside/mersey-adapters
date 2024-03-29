@file:Suppress("UNCHECKED_CAST")

package com.merseyside.adapters.core.feature.selecting

import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.feature.selecting.callback.HasOnItemSelectedListener
import com.merseyside.adapters.core.feature.selecting.callback.OnItemSelectedListener
import com.merseyside.adapters.core.feature.selecting.callback.OnSelectEnabledListener
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList
import com.merseyside.adapters.core.modelList.callback.ModelListCallback
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.extensions.addOrSet
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndSet
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class AdapterSelect<Parent, Model>(
    internal val modelList: ModelList<Parent, Model>,
    selectableMode: SelectableMode,
    isSelectEnabled: Boolean,
    private var isAllowToCancelSelection: Boolean,
    private val forceSelect: Boolean,
    override val workManager: AdapterWorkManager
) : HasOnItemSelectedListener<Parent>, ModelListCallback<Model>, HasAdapterWorkManager, ILogger
        where Model : VM<Parent> {

    var onSelectEnabledListener: OnSelectEnabledListener? = null

    internal var isGroupAdapter: Boolean = false
    internal var selectFirstOnAdd: Boolean = false

    override val selectedListeners: MutableList<OnItemSelectedListener<Parent>> by lazy {
        ArrayList()
    }

    private val selectedList: MutableList<SelectableItem> = ArrayList()

    val selectedCount: Int
        get() = selectedList.size

    var selectableMode: SelectableMode = selectableMode
        set(value) {
            if (field != value) {
                field = value

                if (value == SelectableMode.SINGLE) {
                    if (selectedList.size > 1) {
                        (1 until selectedList.size).forEach { index ->
                            updateItemWithState(selectedList[index], newState = false)
                        }

                        selectedList.removeAll(selectedList.subList(1, selectedList.lastIndex))
                    }
                }
            }
        }

    var isSelectEnabled: Boolean = isSelectEnabled
        set(value) {
            if (value != field) {
                field = value

                modelList.forEach { model ->
                    model.asSelectable().selectState.globalSelectable.compareAndSet(value)
                }

                if (selectedList.isEmpty() && selectableMode == SelectableMode.SINGLE &&
                    !isAllowToCancelSelection
                ) {
                    if (forceSelect) selectFirstItemIfNeed()
                }

                onSelectEnabledListener?.onEnabled(value)
            }
        }

    init {
        modelList.addModelListCallback(this)
    }

    override suspend fun onInserted(models: List<Model>, position: Int, count: Int) {
        initNewModels(models)
        addSelectedItems(models.filterIsInstance<SelectableItem>())
    }

    private fun initNewModels(models: List<Model>) {
        val selectableItems = models.filterIsInstance<SelectableItem>()
        selectableItems.forEach { item ->
            with(item.selectState) {
                selectEvent.observe { changeItemSelectedState(item, true) }
                globalSelectable.compareAndSet(isSelectEnabled)
            }
        }
    }

    override suspend fun onRemoved(models: List<Model>, position: Int, count: Int) {
        removeSelected(models)
    }

    override suspend fun onChanged(
        model: Model,
        position: Int,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
    }

    override suspend fun onMoved(fromPosition: Int, toPosition: Int) {}

    override suspend fun onCleared() {
        selectedList.clear()
    }

    override fun addOnItemSelectedListener(listener: OnItemSelectedListener<Parent>) {
        super.addOnItemSelectedListener(listener)

        selectedList.forEach { item ->
            listener.onSelected(
                item = (item as Model).item,
                isSelected = true,
                isSelectedByUser = false
            )
        }
    }

    fun selectItem(item: Parent): Boolean {
        val selectable = modelList.findModelByItem(item).requireSelectable()
        return changeItemSelectedState(selectable)
    }

    fun getSelectedItem(): Parent? {
        return if (selectedList.isNotEmpty()) {
            selectedList.first().asModel().item
        } else {
            null
        }
    }

    fun getSelectedItems(): List<Parent> {
        return selectedList
            .map { it.asModel() }
            .map { it.item }
    }

    private fun updateItemWithState(
        item: SelectableItem,
        newState: Boolean = !item.isSelected(),
        isSelectedByUser: Boolean = false
    ): Boolean {
        if (item.isSelected() xor newState) {
            return if (item.selectState.setSelectState(newState)) {
                updateSelectedListWithItem(item)
                notifyItemSelected(item, isSelectedByUser)
                true
            } else false
        }

        return false
    }

    private fun updateSelectedListWithItem(item: SelectableItem): Boolean {
        return if (item.isSelected()) {
            selectedList.add(item)
        } else {
            selectedList.remove(item)
        }
    }

    private fun addSelectedItems(list: List<SelectableItem>) {
        val selected = list.filter { it.isSelected() }
        if (selectableMode == SelectableMode.SINGLE) {
            if (selected.isEmpty()) {
                if (isAllowToCancelSelection) return
                else if (forceSelect) selectFirstItemIfNeed()
            } else {
                val lastSelectedItem = selected.last()
                selectedList.addOrSet(0, lastSelectedItem)
                notifyItemSelected(lastSelectedItem, false)

                //make another items not selected
                val wrongSelectedItems = selected.toMutableList().apply { remove(lastSelectedItem) }
                wrongSelectedItems.forEach { item ->
                    if (item.selectState.setSelectState(false)) {
                        notifyItemSelected(item, false)
                    }
                }
            }
        } else {
            selectedList.addAll(selected)
            selected.forEach { notifyItemSelected(it, false) }
        }
    }

    private fun changeItemSelectedState(
        item: SelectableItem,
        isSelectedByUser: Boolean = false
    ): Boolean {

        return with(item) {
            if (canItemBeSelected(item)) {
                when (selectableMode) {
                    SelectableMode.SINGLE -> {
                        if (!isSelected()) {
                            val selectedItem = selectedList.firstOrNull()
                            if (selectedItem != null) {
                                updateItemWithState(
                                    selectedItem,
                                    isSelectedByUser = false
                                )
                            }

                            updateItemWithState(item, isSelectedByUser = isSelectedByUser)
                        } else {
                            if (isAllowToCancelSelection) {
                                updateItemWithState(item, isSelectedByUser = isSelectedByUser)
                            } else false
                        }
                    }

                    SelectableMode.MULTIPLE -> {
                        if (isSelected()) {
                            if (isAllowToCancelSelection) {
                                updateItemWithState(item, isSelectedByUser = isSelectedByUser)
                            } else false
                        } else {
                            updateItemWithState(item, isSelectedByUser = isSelectedByUser)
                        }
                    }
                }
            } else false
        }
    }

    private fun isItemSelected(model: SelectableItem): Boolean {
        return if (canItemBeSelected(model)) {
            model.isSelected()
        } else {
            false
        }
    }

    private fun canItemBeSelected(item: SelectableItem): Boolean {
        return isSelectEnabled && item.selectState.selectable
    }

    private fun selectFirstItemIfNeed() {
        if (isSelectEnabled && !isAllowToCancelSelection) {
            selectFirstSelectableItem()
        }
    }

    fun selectFirstSelectableItem() {
        modelList.forEach { item ->
            if (item.isSelectable()) {
                if (changeItemSelectedState(item.asSelectable())) return
            }
        }
    }

    fun clear() {
        val itemsToRemove = ArrayList(selectedList)

        itemsToRemove.forEach { item ->
            updateItemWithState(item, false)
        }
    }

    @OptIn(ExperimentalContracts::class)
    internal fun Model?.isSelectable(): Boolean {
        contract {
            returns(true) implies (this@isSelectable != null && this@isSelectable is SelectableItem)
        }

        return this != null && this is SelectableItem
    }

    internal fun Model?.asSelectable(): SelectableItem {
        return if (this != null) {
            this as SelectableItem
        } else throw NullPointerException("Tried to cast to Selectable, but model is null!")
    }

    internal fun Model?.requireSelectable(): SelectableItem {
        return if (this != null) {
            (this as? SelectableItem) ?: throw IllegalArgumentException("Selectable item required!")
        } else throw NullPointerException()
    }

    private fun SelectableItem.asModel(): Model {
        return this as Model
    }

    private fun removeSelected(list: List<Model>) {
        if (list.isNotEmpty()) {
            val selectedItemsGoingToRemove: Set<SelectableItem> =
                selectedList.intersect(list.map { it as SelectableItem }.toSet())

            if (selectedItemsGoingToRemove.isNotEmpty()) {
                selectedItemsGoingToRemove.forEach { item ->
                    updateItemWithState(item, false)
                }

                selectedList.removeAll(selectedItemsGoingToRemove)
                //notifyItemsRemoved(selectedItemsGoingToRemove)

                if (!isGroupAdapter && !isAllowToCancelSelection) {
                    selectFirstItemIfNeed()
                }
            }
        }
    }

    private fun notifyItemsSelected(items: Set<SelectableItem>, isSelectedByUser: Boolean) {
        items.forEach { notifyItemSelected(it, isSelectedByUser) }
    }

    private fun notifyItemSelected(item: SelectableItem, isSelectedByUser: Boolean) {
        notifyOnSelected((item.asModel()).item, item.isSelected(), isSelectedByUser)
    }

    override val tag: String = "AdapterSelect"
}