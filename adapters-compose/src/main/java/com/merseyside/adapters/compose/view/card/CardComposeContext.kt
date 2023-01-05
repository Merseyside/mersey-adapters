package com.merseyside.adapters.compose.view.card

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.ViewGroupComposeContext
import com.merseyside.adapters.compose.view.base.SCV

object card {
    context(ComposeContext)
    operator fun invoke(
        contextId: String,
        buildViews: ComposeContext.() -> Unit
    ): CardComposeContext {
        return getOrCreateChildContext(contextId) { id, context, viewLifecycleOwner ->
            CardComposeContext(id, context, viewLifecycleOwner, buildViews)
        }
    }
}

open class CardComposeContext(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    buildViews: ComposeContext.() -> Unit
): ViewGroupComposeContext<SCV>(contextId, context, viewLifecycleOwner, buildViews)