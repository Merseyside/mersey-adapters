package com.merseyside.adapters.core.feature.selecting.group

import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.feature.selecting.AdapterSelect
import com.merseyside.adapters.core.feature.selecting.SelectableMode
import com.merseyside.adapters.core.feature.selecting.callback.HasOnItemSelectedListener
import com.merseyside.adapters.core.feature.selecting.callback.OnItemSelectedListener
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.extensions.isNotZero

class SelectableAdapterGroup<Item>(
    var selectableMode: SelectableMode,
    var isAllowToCancelSelection: Boolean = selectableMode == SelectableMode.MULTIPLE
) : HasOnItemSelectedListener<Item>, HasAdapterWorkManager {

    override lateinit var workManager: AdapterWorkManager

    private val groupSelectedListener = object : OnItemSelectedListener<Item> {
        override fun onSelected(item: Item, isSelected: Boolean, isSelectedByUser: Boolean) {
            when (selectableMode) {
                SelectableMode.SINGLE -> {
                    if (isSelected) {
                        val adaptersWithSelectedItems = getAdaptersWithSelectedItems()

                        if (adaptersWithSelectedItems.isNotEmpty()) {
                            adaptersWithSelectedItems
                                .find { it.getSelectedItem() != item }
                                ?.let { adapter -> workManager.pendingSubTaskWith(adapter) { clear() }}
                        }
                    }
                }

                SelectableMode.MULTIPLE -> {

                }
            }
        }

        override fun onSelectedRemoved(
            adapterList: AdapterSelect<Item, *>,
            items: List<Item>
        ) {
            workManager.doAsync { selectMostAppropriateItem(adapterList) }
        }
    }

    override val selectedListeners: MutableList<OnItemSelectedListener<Item>> =
        mutableListOf(groupSelectedListener)
    private val adapters: MutableList<AdapterSelect<Item, *>> = mutableListOf()

    fun addAsync(adapter: AdapterSelect<Item, *>, onComplete: (Unit) -> Unit = {}) {
        workManager.doAsync(onComplete) { add(adapter) }
    }

    fun add(adapter: AdapterSelect<Item, *>) {
        adapter.isGroupAdapter = true
        adapters.add(adapter)
        selectedListeners.forEach { listener ->
            adapter.addOnItemSelectedListener(listener)
        }

        if (!isAllowToCancelSelection && adapters.size == 1) {
            adapter.selectFirstOnAdd = true
        }
    }

    suspend fun removeAsync(adapter: AdapterSelect<Item, *>, onComplete: (Unit) -> Unit = {}) {
        workManager.doAsync(onComplete) { remove(adapter) }
    }

    suspend fun remove(adapter: AdapterSelect<Item, *>) {
        adapters.remove(adapter)
        if (adapter.selectedCount.isNotZero() &&
            !isAllowToCancelSelection && selectableMode == SelectableMode.SINGLE
        ) {
            selectFirstItem()
        }
    }

    override fun addOnItemSelectedListener(listener: OnItemSelectedListener<Item>) {
        super.addOnItemSelectedListener(listener)
        adapters.forEach {
            it.addOnItemSelectedListener(listener)
        }
    }

    override fun removeOnItemSelectedListener(listener: OnItemSelectedListener<Item>) {
        super.removeOnItemSelectedListener(listener)
        adapters.forEach {
            it.removeOnItemSelectedListener(listener)
        }
    }

    fun getSelectedItems(): List<Item> {
        return adapters.flatMap {
            it.getSelectedItems()
        }
    }

    private fun selectFirstItem() {
        adapters.forEach { adapter ->
            if (adapter.isSelectEnabled) {
                adapter.selectFirstSelectableItem()
                return
            }
        }
    }

    private fun getAdaptersWithSelectedItems(): List<AdapterSelect<Item, *>> {
        return adapters.filter { it.selectedCount.isNotZero() }
    }

    private fun selectMostAppropriateItem(adapter: AdapterSelect<Item, *>) {
        if (!isAllowToCancelSelection) {
            if (adapter.selectedCount.isNotZero()) {
                adapter.selectFirstSelectableItem()
            } else {
                selectFirstItem()
            }
        }
    }

    internal fun clear() {
        val adaptersWithSelectedItems = getAdaptersWithSelectedItems()
        adaptersWithSelectedItems.forEach { adapter -> adapter.clear() }
    }

    fun clearAsync(onComplete: (Unit) -> Unit = {}) {
        workManager.doAsync(onComplete) { clear() }
    }
}