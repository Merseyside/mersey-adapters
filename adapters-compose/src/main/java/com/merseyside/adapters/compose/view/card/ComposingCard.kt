package com.merseyside.adapters.compose.view.card

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.compose.delegate.ViewDelegate
import com.merseyside.adapters.compose.view.list.selectable.SelectableListConfig
import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroup
import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroupStyle
import com.merseyside.adapters.compose.view.viewGroup.dsl.context.ViewGroupComposeContext

open class ComposingCard(
    id: String,
    val configure: SelectableListConfig.() -> Unit,
    override val composingStyle: ComposingCardStyle,
    viewGroupComposeContext: ViewGroupComposeContext<SCV>
) : ComposingViewGroup<ComposingCardStyle>(id, composingStyle, viewGroupComposeContext) {

    override val delegate: ViewDelegate<ComposingCardStyle> = ComposingCardDelegate()

    open val listConfig: SelectableListConfig by lazy { SelectableListConfig().apply(configure) }

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            configure: SelectableListConfig.() -> Unit = {},
            style: ComposingCardStyle.() -> Unit = {},
            initContext: ComposeContext.() -> Unit
        ): ComposingCard {
            val cardContext = card(id, initContext)

            return ComposingCard(id, configure, ComposingCardStyle(context, style), cardContext)
                .addView()
        }
    }
}

open class ComposingCardStyle(context: Context) : ComposingViewGroupStyle(context) {

    @DimenRes
    var cardCornerRadius: Int? = null

    @DimenRes
    var cardElevation: Int? = null

    @ColorRes
    var backgroundCardColor: Int? = null

    var contentPaddings: Paddings? = null

    override val tag: String = "CardStyle"

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingCardStyle.() -> Unit
        ): ComposingCardStyle {
            return ComposingCardStyle(context).apply(init)
        }
    }
}