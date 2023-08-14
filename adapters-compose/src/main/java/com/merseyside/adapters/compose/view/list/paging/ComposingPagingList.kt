package com.merseyside.adapters.compose.view.list.paging

import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.list.simple.ComposingList
import com.merseyside.adapters.compose.view.list.simple.ComposingListStyle
import com.merseyside.adapters.compose.view.list.simple.ListConfig
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.compose.manager.ViewDelegate
import com.merseyside.adapters.compose.view.list.paging.dsl.context.PaginationComposeContext
import com.merseyside.adapters.compose.view.list.paging.dsl.context.PagingContext

class ComposingPagingList<Data>(
    id: String,
    configure: ListConfig.() -> Unit,
    composingStyle: ComposingListStyle,
    listComposeContext: PaginationComposeContext<Data>
): ComposingList(id, configure, composingStyle, listComposeContext) {

    override val delegate: ViewDelegate<ComposingListStyle> = ComposingPagingListDelegate()

    companion object {
        context(ComposeContext) operator fun <Data> invoke(
            id: String,
            configure: ListConfig.() -> Unit = {},
            style: ComposingListStyle.() -> Unit = {},
            initContext: PaginationComposeContext<Data>.() -> Unit
        ): ComposingPagingList<Data> {
            val pagingContext = PagingContext(id, initContext)

            return ComposingPagingList(id, configure, ComposingListStyle(context, style), pagingContext)
                .addView()
        }
    }
}