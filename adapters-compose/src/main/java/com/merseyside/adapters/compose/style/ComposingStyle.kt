package com.merseyside.adapters.compose.style

import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.utils.ext.getDimension
import com.merseyside.utils.ext.getDimensionPixelSize
import com.merseyside.utils.getClassName

abstract class ComposingStyle(override val context: Context) : StyleContract, ILogger {
    var width: Int? = null
    var height: Int? = null

    var margins: Margins? = null
    var paddings: Paddings? = null

    var elevation: Float? = null

    @ColorInt var backgroundColor: Int? = null

    fun setBackgroundColor(@ColorRes color: Int) {
        backgroundColor = ContextCompat.getColor(context, color)
    }

    var clickable: Boolean = true

    fun setWidth(@DimenRes dimen: Int) {
        width = context.getDimensionPixelSize(dimen)
    }

    fun setHeight(@DimenRes dimen: Int) {
        height = context.getDimensionPixelSize(dimen)
    }

    fun setElevation(@DimenRes dimen: Int) {
        elevation = context.getDimension(dimen)
    }

    class Margins(
        @DimenRes val top: Int? = null,
        @DimenRes val bottom: Int? = null,
        @DimenRes val start: Int? = null,
        @DimenRes val end: Int? = null
    ) {
        constructor(@DimenRes margin: Int): this(
            margin, margin, margin, margin
        )

        constructor(@DimenRes horizontal: Int? = null, @DimenRes vertical: Int? = null): this(
            vertical, vertical, horizontal, horizontal
        )

        override fun toString(): String {
            val builder = StringBuilder()
            builder.apply {
                appendLine("*** Margins ***")
                //append("top: ").appendLine(t)
            }

            return builder.toString()
        }
    }

    class Paddings(
        @DimenRes val top: Int,
        @DimenRes val bottom: Int,
        @DimenRes val start: Int,
        @DimenRes val end: Int
    ) {
        constructor(@DimenRes padding: Int): this(
            padding, padding, padding, padding
        )

        constructor(@DimenRes horizontal: Int, @DimenRes vertical: Int): this(
            vertical, vertical, horizontal, horizontal
        )

        override fun toString(): String {
            val builder = StringBuilder()
            builder.apply {
                appendLine("*** Paddings ***")
                //append("top: ").appendLine(t)
            }

            return builder.toString()
        }
    }

    @CallSuper
    open fun getStringBuilder(): StringBuilder {
        val builder = StringBuilder()
        builder.apply {
            append("View style: ").appendLine(getClassName())
            appendLine("*** Composite style ***").appendLine()
            append("width = ").appendLine(width)
            append("height = ").appendLine(height)
            appendLine(margins)
            append("backroundColor = ").appendLine(backgroundColor)
        }

        return builder
    }

    final override fun toString(): String {
        return getStringBuilder().toString()
    }

    companion object {
        const val MATCH_PARENT = -1
        const val WRAP_CONTENT = -2
    }
}