package com.merseyside.adapters.compose.view.text

import android.content.Context
import android.widget.TextView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.merseyLib.kotlin.utils.safeLet
import com.merseyside.utils.ext.setTextSizePx

open class ComposingTextDelegate<View : ComposingText<Style>, Style : ComposingTextStyle, Model : ComposingTextViewModel<View>> :
    ViewDelegateAdapter<View, Style, Model>() {
    override fun applyStyle(
        context: Context,
        holder: ViewHolder<SCV, Model>,
        style: Style
    ) {
        super.applyStyle(context, holder, style)
        val text = holder.root as TextView
        with(text) {
            safeLet(style.textColor) { color -> setTextColor(color) }
            safeLet(style.textSize) { textSize ->
                setTextSizePx(textSize)
            }
            safeLet(style.gravity) { gravity -> setGravity(gravity) }
            safeLet(style.maxLines) { maxLines -> setMaxLines(maxLines) }

            safeLet(style.compoundDrawables) { drawables ->
                with(drawables) {
                    setCompoundDrawablesRelativeWithIntrinsicBounds(get(0), get(1), get(2), get(3))
                }
            }

            text.setTypeface(text.typeface, style.typeface)

        }
    }

    override fun getLayoutIdForItem() = R.layout.view_composing_text
    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingTextViewModel(item) as Model

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingText::class.java
    }
}