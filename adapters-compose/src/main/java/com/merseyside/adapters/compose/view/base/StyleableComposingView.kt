package com.merseyside.adapters.compose.view.base

import androidx.annotation.CallSuper
import com.merseyside.adapters.compose.delegate.ViewDelegate
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.style.StyleableItem
import com.merseyside.adapters.core.base.callback.click.HasOnItemClickListener
import com.merseyside.adapters.core.base.callback.click.OnItemClickListener
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.utils.getClassName

abstract class ComposingView(override val id: String) : Identifiable<String>,
    HasOnItemClickListener<ComposingView> {

    override val clickListeners: MutableList<OnItemClickListener<ComposingView>> by lazy { ArrayList() }

    @CallSuper
    open fun getStringBuilder(): StringBuilder {
        val builder = StringBuilder()
        builder.apply {
            appendLine()
            append("View: ").appendLine(this@ComposingView.getClassName())
            append("id: ").appendLine(id)
        }

        return builder
    }

    final override fun toString(): String {
        return getStringBuilder().toString()
    }
}

abstract class StyleableComposingView<Style : ComposingStyle>(id: String) : ComposingView(id),
    StyleableItem<Style> {

    abstract val delegate: ViewDelegate<Style>
}

typealias SCV = StyleableComposingView<*>