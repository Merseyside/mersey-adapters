package com.merseyside.adapters.compose.view.editTextLayout

import android.content.Context
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.google.android.material.textfield.TextInputLayout
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.merseyLib.kotlin.utils.safeLet

open class ComposingEditTextLayoutDelegate<View : ComposingEditTextLayout<Style>,
        Style : ComposingEditTextLayoutStyle,
        VM : ComposingEditTextLayoutViewModel<View>> :
    ViewDelegateAdapter<View, Style, VM>() {

    override fun applyStyle(context: Context, viewDataBinding: ViewDataBinding, style: Style) {
        super.applyStyle(context, viewDataBinding, style)
        val editTextLayout = viewDataBinding.root as TextInputLayout
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
                editTextLayout.isEnabled = enabled
                editText?.isEnabled = enabled
            }
            safeLet(style.hintText) { textRes ->
                editTextLayout.setHint(textRes)
            }
        }
    }

    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_edit_text
    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingEditTextLayoutViewModel(item) as VM

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingEditTextLayout::class.java
    }

}