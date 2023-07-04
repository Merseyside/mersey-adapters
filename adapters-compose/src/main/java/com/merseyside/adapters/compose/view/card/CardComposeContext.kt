package com.merseyside.adapters.compose.view.card

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.viewGroup.dsl.context.ViewGroupComposeContext
import com.merseyside.adapters.compose.view.base.SCV

object card {
    context(ComposeContext)
    operator fun invoke(
        contextId: String,
        initContext: ComposeContext.() -> Unit
    ): CardComposeContext {
        return getOrCreateChildContext(contextId) { id, context, viewLifecycleOwner ->
            CardComposeContext(id, context, viewLifecycleOwner, initContext)
        }
    }
}

open class CardComposeContext(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: ComposeContext.() -> Unit
): ViewGroupComposeContext<SCV>(contextId, context, viewLifecycleOwner, initContext)