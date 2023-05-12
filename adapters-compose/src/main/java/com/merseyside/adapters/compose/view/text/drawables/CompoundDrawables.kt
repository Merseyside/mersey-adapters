package com.merseyside.adapters.compose.view.text.drawables

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.merseyside.adapters.compose.style.StyleContract
import com.merseyside.merseyLib.kotlin.utils.safeLet

internal interface CompoundDrawables : StyleContract {

    var compoundDrawables: Array<Drawable?>?

    fun setCompoundDrawables(
        left: Drawable? = null, top: Drawable? = null,
        right: Drawable? = null, bottom: Drawable? = null
    ) {
        compoundDrawables = Array(4) { index ->
            when (index) {
                0 -> left
                1 -> top
                2 -> right
                3 -> bottom
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun setCompoundDrawablesRes(
        @DrawableRes left: Int? = null, @DrawableRes top: Int? = null,
        @DrawableRes right: Int? = null, @DrawableRes bottom: Int? = null
    ) {
        setCompoundDrawables(
            safeLet(left) { AppCompatResources.getDrawable(context, it) },
            safeLet(top) { AppCompatResources.getDrawable(context, it) },
            safeLet(right) { AppCompatResources.getDrawable(context, it) },
            safeLet(bottom) { AppCompatResources.getDrawable(context, it) }
        )
    }
}