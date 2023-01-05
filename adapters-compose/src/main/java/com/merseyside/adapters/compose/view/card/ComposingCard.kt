package com.merseyside.adapters.compose.view.card

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.viewProvider.addView
import com.merseyside.adapters.compose.view.list.selectable.SelectableListConfig

open class ComposingCard(
    id: String,
    val configure: SelectableListConfig.() -> Unit,
    override val composingStyle: ComposingCardStyle,
    open val viewList: List<SCV> = emptyList()
) : StyleableComposingView<ComposingCardStyle>(id) {

    override fun getSuitableDelegate():
            ViewDelegateAdapter<out StyleableComposingView<out ComposingCardStyle>, out ComposingCardStyle, *> {
        return ComposingCardDelegate()
    }

    open val listConfig: SelectableListConfig by lazy { SelectableListConfig().apply(configure) }

    companion object {
        context (ComposeContext) operator fun invoke(
            id: String,
            configure: SelectableListConfig.() -> Unit = {},
            style: ComposingCardStyle.() -> Unit = {},
            buildViews: ComposeContext.() -> Unit
        ): ComposingCard {
            val cardContext = card(id, buildViews)
            val views = cardContext.views

            return ComposingCard(id, configure, ComposingCardStyle(context, style), views)
                .addView()
        }
    }
}

open class ComposingCardStyle(context: Context) : ComposingStyle(context) {
    override val tag: String = "CardStyle"

    @DimenRes
    var cardCornerRadius: Int? = null

    @DimenRes
    var cardElevation: Int? = null

    @ColorRes
    var backgroundCardColor: Int? = null

    var contentPaddings: Paddings? = null

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingCardStyle.() -> Unit
        ): ComposingCardStyle {
            return ComposingCardStyle(context).apply(init)
        }
    }
}