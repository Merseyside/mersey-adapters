package com.merseyside.adapters.compose.view.text

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.GravityInt
import androidx.core.content.ContextCompat
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.view.text.drawables.CompoundDrawables
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.utils.ext.getColorFromAttr

open class ComposingText<Style : ComposingTextStyle>(
    id: String,
    override val composingStyle: Style
) : StyleableComposingView<Style>(id) {

    var text: String = ""

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            style: ComposingTextStyle.() -> Unit = {},
            init: Text.() -> Unit
        ): Text {
            return ComposingText(id, ComposingTextStyle(context, style))
                .apply(init)
                .addView()
        }
    }

    override fun getDelegate(): ViewDelegateAdapter<out StyleableComposingView<Style>, Style, *> {
        return ComposingTextDelegate()
    }

    override fun getStringBuilder(): StringBuilder {
        return super.getStringBuilder().apply {
            appendLine()
            append("text: ").appendLine(text)
        }
    }
}

open class ComposingTextStyle(context: Context) : ComposingStyle(context), CompoundDrawables {
    @ColorInt
    var textColor: Int? = null

    var textSize: Int? = null
    @GravityInt var gravity: Int? = null
    var maxLines: Int? = null

    var typeface: Int = Typeface.NORMAL

    override var compoundDrawables: Array<Drawable?>? = null

    fun setTextColor(@ColorRes color: Int) {
        textColor = ContextCompat.getColor(context, color)
    }

    fun setTextColorAttr(@AttrRes color: Int) {
        textColor = context.getColorFromAttr(color)
    }

    fun setTextSize(@DimenRes size: Int) {
        textSize = context.resources.getDimensionPixelSize(size)
    }

    companion object {
        operator fun invoke(context: Context, init: ComposingTextStyle.() -> Unit): ComposingTextStyle {
            return ComposingTextStyle(context).apply(init)
        }
    }

    override val tag: String = "TextStyle"
}

typealias Text = ComposingText<ComposingTextStyle>