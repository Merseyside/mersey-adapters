package com.merseyside.adapters.compose.view.button

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.*
import androidx.appcompat.content.res.AppCompatResources
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.compose.manager.ViewDelegate
import com.merseyside.adapters.compose.view.button.icon.Icon
import com.merseyside.adapters.compose.view.text.ComposingText
import com.merseyside.adapters.compose.view.text.ComposingTextStyle
import com.merseyside.utils.ext.getColorFromAttr
import com.merseyside.utils.ext.getDimensionPixelSize

open class ComposingButton<Style : ComposingButtonStyle>(
    id: String,
    override val composingStyle: Style
) : ComposingText<Style>(id, composingStyle) {

    @Suppress("UNCHECKED_CAST")
    override val delegate: ViewDelegate<Style> = ComposingButtonDelegate()

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

    var isEnabled: Boolean? = null

    var textColorStateList: ColorStateList? = null
    var textAllCaps: Boolean? = null
    @GravityInt
    var textGravity: Int? = null

    var backgroundTint: ColorStateList? = null
    var cornerRadius: Int? = null

    /** Use Icon builder */
    var icon: Icon? = null
        private set

    /**
     * Builder for button's icon
     * @see Icon
     */
    fun Icon(@DrawableRes drawable: Int, init: Icon.() -> Unit): Icon {
        return Icon(context, drawable, init).also {
            icon = it
        }

    }

    fun setBackgroundTintRes(@ColorRes colorRes: Int?) {
        colorRes?.let {
            backgroundTint =
                AppCompatResources.getColorStateList(context, colorRes)
        }
    }

    fun setBackgroundTint(@ColorInt color: Int) {
        backgroundTint = ColorStateList.valueOf(color)
    }

    fun setBackgroundTintAttr(@AttrRes color: Int) {
        setBackgroundTint(context.getColorFromAttr(color))
    }

    fun setTextColorStateList(@ColorRes stateList: Int) {
        textColorStateList = AppCompatResources.getColorStateList(context, stateList)
    }

    fun setCornerRadius(@DimenRes radiusRes: Int) {
        cornerRadius = context.getDimensionPixelSize(radiusRes)
    }

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