package com.merseyside.adapters.compose.view.button

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.viewProvider.addView
import com.merseyside.adapters.compose.view.text.ComposingText
import com.merseyside.adapters.compose.view.text.ComposingTextStyle

open class ComposingButton<Style : ComposingButtonStyle>(
    id: String,
    override val composingStyle: Style
) : ComposingText<Style>(id, composingStyle) {

    @Suppress("UNCHECKED_CAST")
    override fun getSuitableDelegate(): ViewDelegateAdapter<out ComposingButton<Style>, Style, *> {
        return ComposingButtonDelegate()
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
    var textColorStateList: ColorStateList? = null
    var textAllCaps: Boolean? = null

    fun setBackgroundTint(@ColorRes colorRes: Int?) {
        colorRes?.let {
            backgroundTint =
                AppCompatResources.getColorStateList(context, colorRes)
        }
    }

    fun setTextColorStateList(@ColorRes stateList: Int) {
        textColorStateList = AppCompatResources.getColorStateList(context, stateList)
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