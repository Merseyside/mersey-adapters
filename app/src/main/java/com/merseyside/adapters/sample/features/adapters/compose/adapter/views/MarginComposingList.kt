package com.merseyside.adapters.sample.features.adapters.compose.adapter.views

import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.list.simple.ComposingList
import com.merseyside.adapters.compose.view.list.simple.ComposingListStyle
import com.merseyside.adapters.compose.view.list.simple.ListConfig
import com.merseyside.adapters.sample.R

object MarginComposingList {

    private fun initWithDefaults(init: ComposingListStyle.() -> Unit): ComposingListStyle.() -> Unit = {
        margins = ComposingStyle.Margins(start = R.dimen.normal_spacing)
        init()
    }

    context (ComposeContext) operator fun invoke(
        id: String,
        style: ComposingListStyle.() -> Unit = {},
        configure: ListConfig.() -> Unit = {},
        buildViews: ComposeContext.() -> Unit
    ): ComposingList {
        return ComposingList(id, configure, initWithDefaults(style), buildViews)
    }
}