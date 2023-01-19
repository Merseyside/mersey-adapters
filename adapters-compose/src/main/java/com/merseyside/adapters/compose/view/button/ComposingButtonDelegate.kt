package com.merseyside.adapters.compose.view.button

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.text.ComposingTextDelegate
import com.merseyside.merseyLib.kotlin.utils.safeLet
import android.widget.Button as ButtonView

class ComposingButtonDelegate : ComposingTextDelegate<Button, ComposingButtonStyle, ComposingButtonViewModel>() {

    override fun applyStyle(
        context: Context,
        viewDataBinding: ViewDataBinding,
        style: ComposingButtonStyle
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
        }
    }

    override fun getBindingVariable() = BR.model
    override fun createItemViewModel(item: Button) = ComposingButtonViewModel(item)
    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_button

    override fun isResponsibleForItemClass(clazz: Class<out SCV>): Boolean {
        return clazz == ComposingButton::class.java
    }
}