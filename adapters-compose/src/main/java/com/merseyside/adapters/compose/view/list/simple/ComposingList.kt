package com.merseyside.adapters.compose.view.list.simple

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.view.list.dsl.context.ListComposeContext
import com.merseyside.adapters.compose.view.list.dsl.context.ListContext
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.compose.view.viewGroup.ComposingViewGroupStyle
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.core.base.callback.HasOnItemClickListener
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.base.callback.OnItemClickListener
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.model.VM
import com.merseyside.utils.layoutManager.LinearLayoutManager

open class ComposingList(
    id: String,
    val configure: ListConfig.() -> Unit,
    override val composingStyle: ComposingListStyle,
    internal val listComposeContext: ListComposeContext
) : StyleableComposingView<ComposingListStyle>(id) {

    open val listConfig: ListConfig by lazy { ListConfig().apply(configure) }

    override fun getSuitableDelegate(): ViewDelegateAdapter<out StyleableComposingView<out ComposingListStyle>, out ComposingListStyle, *> {
        return ComposingListDelegate()
    }

    companion object {
        context(ComposeContext) operator fun invoke(
            id: String,
            configure: ListConfig.() -> Unit = {},
            style: ComposingListStyle.() -> Unit = {},
            initContext: ComposeContext.() -> Unit = {}
        ): ComposingList {
            val listContext = ListContext(id, initContext)

            return ComposingList(id, configure, ComposingListStyle(context, style), listContext)
                .addView()
        }
    }
}

open class ListConfig : HasOnItemClickListener<SCV> {

    internal var attachToRecyclerViewListeners: MutableList<OnAttachToRecyclerViewListener> = mutableListOf()
    override val clickListeners: MutableList<OnItemClickListener<SCV>> = ArrayList()

    fun addOnAttachToRecyclerViewListener(listener: OnAttachToRecyclerViewListener) {
        attachToRecyclerViewListeners.add(listener)
    }

    var adapterConfig: AdapterConfig<SCV, VM<SCV>>.() -> Unit = {}

    var decorator: RecyclerView.ItemDecoration? = null

    var layoutManager: (context: Context) -> LayoutManager =
        { context -> LinearLayoutManager(context, RecyclerView.VERTICAL, reverseLayout = false) }

}

open class ComposingListStyle(context: Context) : ComposingViewGroupStyle(context) {

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingListStyle.() -> Unit
        ): ComposingListStyle {
            return ComposingListStyle(context).apply(init)
        }
    }

    override val tag: String = "ListStyle"
}