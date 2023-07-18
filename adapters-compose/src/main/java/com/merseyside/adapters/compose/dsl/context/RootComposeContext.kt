package com.merseyside.adapters.compose.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.dsl.context.ListComposeContext
import com.merseyside.adapters.core.model.VM

class RootComposeContext(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: Composer
) : ListComposeContext(contextId, context, viewLifecycleOwner, initContext)

internal object compose {
    operator fun invoke(
        context: Context,
        viewLifecycleOwner: LifecycleOwner,
        rootAdapter: ViewCompositeAdapter<SCV, VM<SCV>>,
        initContext: Composer
    ): RootComposeContext {
        return RootComposeContext(rootContextId, context, viewLifecycleOwner, initContext).apply {
            setRelativeAdapter(rootAdapter)
        }
    }

    private const val rootContextId = "root_context"
}

internal typealias Composer = ComposeContext.() -> Unit