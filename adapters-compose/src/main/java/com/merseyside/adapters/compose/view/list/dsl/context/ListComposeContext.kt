package com.merseyside.adapters.compose.view.list.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.viewGroup.dsl.context.ViewGroupComposeContext
import com.merseyside.adapters.compose.view.base.SCV

object listContext {
    context(ComposeContext)
    operator fun invoke(
        contextId: String,
        initContext: ComposeContext.() -> Unit
    ): ListComposeContext {
        return getOrCreateChildContext(contextId) { id, context, viewLifecycleOwner ->
            ListComposeContext(id, context, viewLifecycleOwner, initContext)
        }
    }
}

open class ListComposeContext(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: ComposeContext.() -> Unit
): ViewGroupComposeContext<SCV>(contextId, context, viewLifecycleOwner, initContext)