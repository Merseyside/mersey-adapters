package com.merseyside.adapters.compose.view.button

import android.content.Context
import androidx.core.view.updateLayoutParams
import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.text.ComposingTextDelegate
import com.merseyside.merseyLib.kotlin.utils.safeLet
import android.widget.Button as ButtonView

open class ComposingButtonDelegate<View : ComposingButton<Style>,
        Style : ComposingButtonStyle, VM : ComposingButtonViewModel<View>> :
    ComposingTextDelegate<View, Style, VM>() {

    override fun applyStyle(
        context: Context,
        viewDataBinding: ViewDataBinding,
        style: Style
    ) {
        super.applyStyle(context, viewDataBinding, style)
        val button = viewDataBinding.root as ButtonView

        with(button) {
            safeLet(style.backgroundTint) { color ->
                backgroundTintList = color
            }
            safeLet(style.isEnabled) { enabled ->
                isEnabled = enabled
            }
            safeLet(style.textColorStateList) { stateList ->
                setTextColor(stateList)
            }
            safeLet(style.textAllCaps) { allCaps ->
                isAllCaps = allCaps
            }
        }
    }

    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingButtonViewModel(item) as VM
    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_button

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingButton::class.java
    }
}