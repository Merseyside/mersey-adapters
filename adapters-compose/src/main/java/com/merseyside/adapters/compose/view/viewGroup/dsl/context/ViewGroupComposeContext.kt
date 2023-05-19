package com.merseyside.adapters.compose.view.viewGroup.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.dsl.context.ViewComposeContext
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.model.VM

abstract class ViewGroupComposeContext<View : SCV>(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: ViewComposeContext<View>.() -> Unit
) : ViewComposeContext<View>(contextId, context, viewLifecycleOwner, initContext) {

    override fun onContextStateChanged() {
        clearViews()
        initContext()
    }

    override fun onViewsChanged(adapter: ViewCompositeAdapter<SCV, VM<SCV>>, data: List<View>) {
        adapter.updateAsync(data)
    }
}