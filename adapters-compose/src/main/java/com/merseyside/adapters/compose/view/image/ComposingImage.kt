package com.merseyside.adapters.compose.view.image

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView.ScaleType
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.StyleableComposingView

class ComposingImage<Style : ComposingImageStyle>(
    id: String,
    override val composingStyle: Style
): StyleableComposingView<Style>(id) {

    lateinit var drawable: Drawable

    override fun getDelegate(): ViewDelegateAdapter<out StyleableComposingView<out Style>, out Style, *> {
        return ComposingImageDelegate()
    }

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            style: ComposingImageStyle.() -> Unit = {},
            init: Image.() -> Unit
        ): Image {
            return ComposingImage(id, ComposingImageStyle(context, style))
                .apply(init)
                .addView()
        }
    }


}

open class ComposingImageStyle(context: Context) : ComposingStyle(context) {

    var adjustViewBounds: Boolean = false
    var scaleType: ScaleType = ScaleType.CENTER



    companion object {
        operator fun invoke(context: Context, init: ComposingImageStyle.() -> Unit): ComposingImageStyle {
            return ComposingImageStyle(context).apply(init)
        }
    }

    override val tag: String = "ImageStyle"
}

typealias Image = ComposingImage<ComposingImageStyle>