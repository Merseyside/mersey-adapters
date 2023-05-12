package com.merseyside.adapters.compose.view.button

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.google.android.material.button.MaterialButton
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.text.ComposingTextDelegate
import com.merseyside.merseyLib.kotlin.utils.safeLet

open class ComposingButtonDelegate<View : ComposingButton<Style>,
        Style : ComposingButtonStyle, VM : ComposingButtonViewModel<View>> :
    ComposingTextDelegate<View, Style, VM>() {

    override fun applyStyle(
        context: Context,
        viewDataBinding: ViewDataBinding,
        style: Style
    ) {
        super.applyStyle(context, viewDataBinding, style)
        val button = viewDataBinding.root as MaterialButton

        with(button) {

            safeLet(style.isEnabled) { enabled ->
                isEnabled = enabled
            }
            safeLet(style.textColorStateList) { stateList ->
                setTextColor(stateList)
            }
            safeLet(style.textAllCaps) { allCaps ->
                isAllCaps = allCaps
            }
            safeLet(style.textGravity) { textGravity ->
                gravity = textGravity
            }

            safeLet(style.backgroundTint) { color ->
                backgroundTintList = color
            }

            safeLet(style.cornerRadius) { radius ->
                cornerRadius = radius
            }

            safeLet(style.icon) { i ->
                with(icon) {
                    icon = i.icon
                    iconGravity = i.iconGravity
                    iconPadding = i.iconPadding
                    safeLet(i.iconTint) { iconTint = it }
                }
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