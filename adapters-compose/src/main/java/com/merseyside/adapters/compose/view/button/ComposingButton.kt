package com.merseyside.adapters.compose.view.button

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.viewProvider.addView
import com.merseyside.adapters.compose.view.text.ComposingText
import com.merseyside.adapters.compose.view.text.ComposingTextStyle

open class ComposingButton<Style : ComposingButtonStyle>(
    id: String,
    override val composingStyle: Style
) : ComposingText<Style>(id, composingStyle) {

    @Suppress("UNCHECKED_CAST")
    override fun getSuitableDelegate(): ViewDelegateAdapter<out StyleableComposingView<Style>, Style, *> {
        return ComposingButtonDelegate() as ViewDelegateAdapter<out StyleableComposingView<Style>, Style, *>
    }

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            style: ComposingButtonStyle.() -> Unit = {},
            init: Button.() -> Unit
        ): Button {
            return Button(id, ComposingButtonStyle(context, style))
                .apply(init)
                .addView()
        }
    }
}

open class ComposingButtonStyle(context: Context) : ComposingTextStyle(context) {

    var backgroundTint: ColorStateList? = null

    fun setBackgroundTint(@ColorRes colorRes: Int?) {
        colorRes?.let {
            backgroundTint =
                AppCompatResources.getColorStateList(context, colorRes)
        }
    }

    var isEnabled: Boolean? = null

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingButtonStyle.() -> Unit
        ): ComposingButtonStyle {
            return ComposingButtonStyle(context).apply(init)
        }
    }
}

typealias Button = ComposingButton<ComposingButtonStyle>