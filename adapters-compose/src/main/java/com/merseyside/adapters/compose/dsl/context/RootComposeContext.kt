package com.merseyside.adapters.compose.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.viewGroup.dsl.context.ViewGroupComposeContext
import com.merseyside.adapters.core.model.VM

class RootComposeContext<View : SCV>(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: ViewComposeContext<View>.() -> Unit
) : ViewGroupComposeContext<View>(contextId, context, viewLifecycleOwner, initContext)

internal object compose {
    operator fun invoke(
        context: Context,
        viewLifecycleOwner: LifecycleOwner,
        rootAdapter: ViewCompositeAdapter<SCV, VM<SCV>>,
        initContext: ComposeContext.() -> Unit
    ): RootComposeContext<SCV> {
        return RootComposeContext(rootContextId, context, viewLifecycleOwner, initContext).apply {
            setRelativeAdapter(rootAdapter)
        }
    }

    private const val rootContextId = "root_context"
}