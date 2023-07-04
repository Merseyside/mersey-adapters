package com.merseyside.adapters.compose.view.editTextLayout

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.merseyLib.kotlin.utils.safeLet

open class ComposingEditTextLayoutDelegate<View : ComposingEditTextLayout<Style>,
        Style : ComposingEditTextLayoutStyle,
        Model : ComposingEditTextLayoutViewModel<View>> :
    ViewDelegateAdapter<View, Style, Model>() {

    override fun applyStyle(context: Context, holder: ViewHolder<SCV, Model>, style: Style) {
        super.applyStyle(context, holder, style)
        val editTextLayout = holder.root as TextInputLayout
        with(editTextLayout) {
            id = android.view.View.generateViewId()
            safeLet(style.strokeColor) { color ->
                setBoxStrokeColorStateList(
                    AppCompatResources.getColorStateList(
                        context,
                        color
                    )
                )
            }
            safeLet(style.textColor) { color ->
                editText?.setTextColor(color)
            }
            safeLet(style.boxStrokeWidth) { width ->
                boxStrokeWidth = context.resources.getDimensionPixelSize(width)
            }
            safeLet(style.endIconDrawable) { drawable ->
                endIconDrawable = ContextCompat.getDrawable(context, drawable)
            }
            safeLet(style.inputType) { inputType ->
                editText?.inputType = inputType
            }
            safeLet(style.isFocusable) { isFocusable ->
                editText?.isFocusable = isFocusable
            }
            safeLet(style.startIconDrawable) { drawable ->
                startIconDrawable = ContextCompat.getDrawable(context, drawable)
            }
            safeLet(style.isEnabled) { enabled ->
                isEnabled = enabled
                editText?.isEnabled = enabled
            }
            safeLet(style.hintText) { text ->
                hint = text
            }
            safeLet(style.lines) { count ->
                editText?.setLines(count)
            }
            safeLet(style.gravityText) { gravity ->
                editText?.gravity = gravity
            }
            safeLet(style.isVerticalScrollbar) { isVertical ->
                editText?.isVerticalScrollBarEnabled = isVertical
            }
            safeLet(style.isHorizontalScrollbar) { isHorizontal ->
                editText?.isHorizontalScrollBarEnabled = isHorizontal
            }
        }
    }

    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_edit_text
    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingEditTextLayoutViewModel(item) as Model

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingEditTextLayout::class.java
    }

}