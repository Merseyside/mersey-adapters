package com.merseyside.adapters.compose.viewProvider

import com.merseyside.merseyLib.kotlin.observable.EventObservableField
import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ObservableField
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndSetNullable
import com.merseyside.merseyLib.kotlin.observable.ext.toEventObservableField

abstract class ComposeState<T>(val propertyName: String) {

    internal abstract val observableField: ObservableField<T>
    internal val onChangeEvent: EventObservableField by lazy { observableField.toEventObservableField() }

    abstract val value: T?

    private var closure: Closure = {}

    fun close() {
        closure()
    }

    fun setClosure(closure: Closure) {
        this.closure = closure
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

internal typealias Closure = () -> Unit

class MutableComposeState<T>(propertyName: String, initValue: T): ComposeState<T>(propertyName) {

    override val observableField = MutableObservableField(initValue)

    override var value: T?
        get() = observableField.value
        set(value) { observableField.compareAndSetNullable(value) }
}