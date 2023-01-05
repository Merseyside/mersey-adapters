package com.merseyside.adapters.compose.view.editTextLayout

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.viewProvider.addView

open class ComposingEditTextLayout<Style : ComposingEditTextLayoutStyle>(
    id: String,
    override val composingStyle: Style
) : StyleableComposingView<Style>(id) {

    var text: String = ""
    var isClearFocus = false
    var hintText: String = ""
    var textWatcherCallback: (String) -> Unit = {}

    override fun getSuitableDelegate(): ViewDelegateAdapter<out StyleableComposingView<Style>, Style, *> {
        return ComposingEditTextLayoutDelegate()
    }

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            style: ComposingEditTextLayoutStyle.() -> Unit = {},
            init: EditTextLayout.() -> Unit
        ): EditTextLayout {
            return EditTextLayout(id, ComposingEditTextLayoutStyle(context, style))
                .apply(init)
                .addView()
        }
    }

}

open class ComposingEditTextLayoutStyle(context: Context) : ComposingStyle(context) {

    override val tag: String = "EditTextStyle"

    @ColorRes
    var strokeColor: Int? = null

    @ColorRes
    var textColor: Int? = null

    @DimenRes
    var boxStrokeWidth: Int? = null

    @DimenRes
    var boxStrokeWidthFocused: Int? = null

    @DrawableRes
    var endIconDrawable: Int? = null

    @DrawableRes
    var startIconDrawable: Int? = null

    var isClickable: Boolean? = null
    var isFocusable: Boolean? = null
    var isEnabled: Boolean? = null

    @StringRes
    var hintText: Int? = null

    var inputType: Int? = null

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingEditTextLayoutStyle.() -> Unit
        ): ComposingEditTextLayoutStyle {
            return ComposingEditTextLayoutStyle(context).apply(init)
        }
    }
}

typealias EditTextLayout = ComposingEditTextLayout<ComposingEditTextLayoutStyle>