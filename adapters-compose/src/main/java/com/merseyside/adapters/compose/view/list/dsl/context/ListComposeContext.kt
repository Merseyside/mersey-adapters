package com.merseyside.adapters.compose.view.list.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.ViewComposeContext
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.model.VM

object ListContext {
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
): ViewComposeContext<SCV>(contextId, context, viewLifecycleOwner, initContext) {
    override fun onContextStateChanged() {
        clearViews()
        initContext()
    }

    override fun onViewsChanged(adapter: ViewCompositeAdapter<SCV, VM<SCV>>, data: List<SCV>) {
        adapter.updateAsync(data)
    }
}