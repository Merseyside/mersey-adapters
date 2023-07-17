package com.merseyside.adapters.compose.adapter

import android.content.Context
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.Composer
import com.merseyside.adapters.compose.dsl.context.compose
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.merseyLib.kotlin.coroutines.utils.uiDispatcher
import kotlinx.coroutines.withContext


interface HasCompositeAdapter {

    var rootContext: ComposeContext

    val adapter: ViewCompositeAdapter<SCV, ViewAdapterViewModel>

    val context: Context
    val viewLifecycleOwner: LifecycleOwner

    @MainThread
    suspend fun composeScreen(): Composer

    @InternalAdaptersApi
    suspend fun composeInternal() {
        withContext(uiDispatcher) {
            rootContext = compose(context, viewLifecycleOwner, adapter, composeScreen())
        }
    }

    fun showViews(views: List<SCV>) {
        adapter.updateAsync(views)
    }

    @OptIn(InternalAdaptersApi::class)
    fun invalidateAsync(onComplete: (Unit) -> Unit = {}) {
        adapter.doAsync(onComplete) { invalidate() }
    }

    fun clear() {
        val onComplete: (Unit) -> Unit = { rootContext.clear() }
        adapter.doAsync(onComplete) {
            this@HasCompositeAdapter.adapter.delegatesManager.resetDelegates()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <View : SCV> findViewById(id: String): View? {
        return adapter.findViewById(id)
    }

    @InternalAdaptersApi
    suspend fun invalidate() {
        composeInternal()
    }
}