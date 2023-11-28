package com.merseyside.adapters.core.feature.expanding

import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ObservableField
import com.merseyside.merseyLib.kotlin.observable.SingleObservableEvent
import com.merseyside.merseyLib.kotlin.observable.ext.combineFields
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndSet
import com.merseyside.merseyLib.kotlin.observable.ext.valueNotNull

class ExpandState(expanded: Boolean = false, expandable: Boolean = true) {

    internal var globalExpandable = MutableObservableField(true)
    private val itemExpandable = MutableObservableField(expandable)

    val expandedObservable = MutableObservableField(expanded)

    internal val expandEvent = SingleObservableEvent()

    val expandableObservable: ObservableField<Boolean> = combineFields(
        globalExpandable, itemExpandable
    ) { first, second -> first && second }

    private var listener: OnExpandStateListener? = null

    var expanded: Boolean = expanded
        private set(value) {
            if (field != value) {
                field = value

                expandedObservable.compareAndSet(value)
                listener?.onExpand(value)
            }
        }

    var expandable: Boolean
        get() = expandableObservable.valueNotNull()
        set(value) { itemExpandable.compareAndSet(value) }

    init {
        expandableObservable.observe { value ->
            listener?.onExpandable(value)
        }
    }

    fun setOnExpandStateListener(listener: OnExpandStateListener) {
        this.listener = listener
    }

    fun expand() {
        expandEvent.call()
    }

    fun expand(state: Boolean) {
        if (expanded != state) expand()
    }

    private fun onExpand(state: Boolean): Boolean {
        return listener?.onExpand(state) ?: true
    }

    internal fun setExpandState(state: Boolean): Boolean {
        return if (onExpand(state)) {
            expanded = state
            true
        } else false
    }

    interface OnExpandStateListener {
        fun onExpand(expanded: Boolean): Boolean = true

        fun onExpandable(expandable: Boolean)
    }
}