package com.merseyside.adapters.core.feature.selecting

import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.SingleObservableEvent
import com.merseyside.merseyLib.kotlin.observable.ObservableField
import com.merseyside.merseyLib.kotlin.observable.ext.combineFields
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndSet
import com.merseyside.merseyLib.kotlin.observable.ext.valueNotNull

class SelectState(selected: Boolean = false, selectable: Boolean = true) {

    internal var globalSelectable = MutableObservableField(true)
    private val itemSelectable = MutableObservableField(selectable)

    internal val selectEvent = SingleObservableEvent()

    val selectedObservable = MutableObservableField(selected)
    val selectableObservable: ObservableField<Boolean> =
        combineFields(globalSelectable, itemSelectable) { first, second ->
            first && second
        }

    private var listener: OnSelectStateListener? = null

    var selected: Boolean = selected
        private set(value) {
            if (field != value) {
                field = value

                selectedObservable.compareAndSet(value)
                listener?.onSelected(value)
            }
        }

    var selectable: Boolean
        get() = selectableObservable.valueNotNull()
        set(value) {
            itemSelectable.compareAndSet(value)
        }

    init {
        selectableObservable.observe { value ->
            listener?.onSelectable(value)
        }
    }

    fun select() {
        selectEvent.call()
    }

    fun select(state: Boolean) {
        if (selected != state) select()
    }

    internal fun setSelectState(state: Boolean): Boolean {
        return if (onSelect(state)) {
            selected = state
            true
        } else false
    }

    private fun onSelect(state: Boolean): Boolean {
        return listener?.onSelected(state) ?: true
    }

    fun setOnSelectStateListener(listener: OnSelectStateListener) {
        this.listener = listener
    }

    interface OnSelectStateListener {
        fun onSelected(selected: Boolean): Boolean = true

        fun onSelectable(selectable: Boolean)
    }
}