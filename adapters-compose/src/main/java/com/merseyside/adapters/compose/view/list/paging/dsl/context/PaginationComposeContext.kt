package com.merseyside.adapters.compose.view.list.paging.dsl.context

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.asLiveData
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.dsl.context.ListComposeContext
import com.merseyside.adapters.core.model.VM
import kotlinx.coroutines.flow.*

object PagingContext {
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

    lateinit var viewProvider: (Data) -> List<SCV>?

    private val nextLiveData by lazy { onNextPage.map(viewProvider).filterNotNull().asLiveData() }
    private val prevLiveData by lazy { onPrevPage.map(viewProvider).filterNotNull().asLiveData() }

    override fun onInitAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        super.onInitAdapter(adapter)
        checkValid()

        nextLiveData.observe(lifecycleOwner) { views ->
            updateViews { current ->
                current + views
            }
        }

        if (this::onPrevPage.isInitialized) {
            prevLiveData.observe(lifecycleOwner) { views ->
                updateViews { current -> views + current }
            }
        }
    }

    fun resetPaging() = mutableState {
        clearViews()
    }

    override fun clear() {
        super.clear()

        nextLiveData.removeObservers(lifecycleOwner)
        if (this::onPrevPage.isInitialized) prevLiveData.removeObservers(lifecycleOwner)
    }


    private fun checkValid() {
        if (!this::onNextPage.isInitialized) throw IllegalStateException("onNextPage not set!")
        if (!this::viewProvider.isInitialized) throw IllegalStateException("viewProvider not set!")
    }

    override val tag: String = "PagingContext_$contextId"
}