package com.merseyside.adapters.compose.view.button.icon

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton
import com.merseyside.utils.ext.getDimensionPixelSize

class Icon(private val context: Context, var icon: Drawable) {

    constructor(context: Context, @DrawableRes drawable: Int) : this(
        context,
        AppCompatResources.getDrawable(
            context,
            drawable
        ) ?: throw NullPointerException("Drawable res not found!")
    )


    var iconTint: ColorStateList? = null

    @MaterialButton.IconGravity
    var iconGravity: Int = MaterialButton.ICON_GRAVITY_START
    var iconPadding: Int = 0

    fun setIconPaddingRes(@DimenRes dimen: Int) {
        iconPadding = context.getDimensionPixelSize(dimen)
    }

    fun setIconTint(@ColorRes color: Int) {
        iconTint = AppCompatResources.getColorStateList(context, color)
    }

    companion object {
        operator fun invoke(
            context: Context,
            @DrawableRes drawableRes: Int,
            init: Icon.() -> Unit
        ): Icon {
            return Icon(context, drawableRes).apply(init)
        }

        const val ICON_GRAVITY_START = 0x1

        /**
         * Gravity used to position the icon in the center of the view at the start of the text
         *
         * @see .setIconGravity
         * @see .getIconGravity
         */
        const val ICON_GRAVITY_TEXT_START = 0x2

        /**
         * Gravity used to position the icon at the end of the view.
         *
         * @see .setIconGravity
         * @see .getIconGravity
         */
        const val ICON_GRAVITY_END = 0x3

        /**
         * Gravity used to position the icon in the center of the view at the end of the text
         *
         * @see .setIconGravity
         * @see .getIconGravity
         */
        const val ICON_GRAVITY_TEXT_END = 0x4

        /**
         * Gravity used to position the icon at the top of the view.
         *
         * @see .setIconGravity
         * @see .getIconGravity
         */
        const val ICON_GRAVITY_TOP = 0x10

        /**
         * Gravity used to position the icon in the center of the view at the top of the text
         *
         * @see .setIconGravity
         * @see .getIconGravity
         */
        const val ICON_GRAVITY_TEXT_TOP = 0x20
    }


}