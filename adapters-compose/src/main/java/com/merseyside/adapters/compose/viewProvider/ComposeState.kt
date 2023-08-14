package com.merseyside.adapters.compose.viewProvider

import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ObservableField
import com.merseyside.merseyLib.kotlin.observable.SingleObservableEvent
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndSetNullable

abstract class ComposeState<T>(val propertyName: String) {

    internal abstract val observableField: ObservableField<T>
    internal val onChangeEvent = SingleObservableEvent()

    abstract val value: T?

    internal fun observe(observer: (T) -> Unit) {
        observableField.observe(ignoreCurrent = true, observer)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ComposeState<*>) return false

        return propertyName == other.propertyName
    }

    override fun hashCode(): Int {
        return propertyName.hashCode()
    }
}

class MutableComposeState<T>(propertyName: String, initValue: T): ComposeState<T>(propertyName) {

    override val observableField = MutableObservableField(initValue)

    override var value: T?
        get() = observableField.value
        set(value) {
            observableField.compareAndSetNullable(value)
            onChangeEvent.call()
        }
}