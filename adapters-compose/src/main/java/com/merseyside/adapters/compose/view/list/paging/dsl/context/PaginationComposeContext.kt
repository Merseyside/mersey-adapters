package com.merseyside.adapters.compose.view.list.paging.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStarted
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.dsl.context.ListComposeContext
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.coroutines.flow.ext.mapList
import com.merseyside.merseyLib.time.units.Millis
import com.merseyside.utils.delayedMainThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object pagingContext {
    context(ComposeContext)
            @Suppress("UNCHECKED_CAST")
            operator fun <Data> invoke(
        contextId: String,
        initContext: PaginationComposeContext<Data>.() -> Unit
    ): PaginationComposeContext<Data> {
        return getOrCreateChildContext(contextId) { id, context, viewLifecycleOwner ->
            PaginationComposeContext(id, context, viewLifecycleOwner) {
                this as PaginationComposeContext<Data>
                initContext()
            }
        }
    }
}

class PaginationComposeContext<Data>(
    contextId: String,
    context: Context,
    viewLifecycleOwner: LifecycleOwner,
    initContext: ComposeContext.() -> Unit
) : ListComposeContext(contextId, context, viewLifecycleOwner, initContext) {

    lateinit var onNextPage: Flow<Data>
    lateinit var onPrevPage: Flow<Data>

    lateinit var viewProvider: (Data) -> List<SCV>

    override fun onInitAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        super.onInitAdapter(adapter)
        checkValid()
        "init adapter".log("kek")

        onNextPage.map(viewProvider).asLiveData()
            .observe(viewLifecycleOwner) { views ->
                "on next page".log("kek")
                updateViews { current ->
                    current + views
                }
            }

        if (this::onPrevPage.isInitialized) {
            onPrevPage.map(viewProvider).asLiveData().observe(viewLifecycleOwner) { views ->
                updateViews { current -> views + current }
            }
        }
    }

    fun resetPaging() = mutableState {
        clearViews()
    }

    private fun checkValid() {
        if (!this::onNextPage.isInitialized) throw IllegalStateException("onNextPage not set!")
        if (!this::viewProvider.isInitialized) throw IllegalStateException("viewProvider not set!")
    }

}