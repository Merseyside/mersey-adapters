package com.merseyside.adapters.compose.view.list.selectable

import android.content.Context
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.addView
import com.merseyside.adapters.compose.manager.ViewDelegate
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.list.dsl.context.ListComposeContext
import com.merseyside.adapters.compose.view.list.dsl.context.ListContext
import com.merseyside.adapters.compose.view.list.simple.ComposingList
import com.merseyside.adapters.compose.view.list.simple.ComposingListStyle
import com.merseyside.adapters.compose.view.list.simple.ListConfig
import com.merseyside.adapters.core.feature.selecting.SelectableMode
import com.merseyside.adapters.core.feature.selecting.callback.HasOnItemSelectedListener
import com.merseyside.adapters.core.feature.selecting.callback.OnItemSelectedListener

@Suppress("UNCHECKED_CAST")
class ComposingSelectableList(
    id: String,
    configure: SelectableListConfig.() -> Unit,
    composingStyle: ComposingSelectableListStyle,
    listComposeContext: ListComposeContext
) : ComposingList(id, configure as ListConfig.() -> Unit, composingStyle, listComposeContext) {

    override val listConfig: SelectableListConfig by lazy { SelectableListConfig().apply(configure) }

    override val delegate: ViewDelegate<ComposingListStyle> = ComposingSelectableListDelegate()

    companion object {
        context(ComposeContext) operator fun invoke(
            id: String,
            style: ComposingSelectableListStyle.() -> Unit = {},
            configure: SelectableListConfig.() -> Unit = {},
            initContext: ComposeContext.() -> Unit
        ): ComposingSelectableList {
            val listContext = ListContext(id, initContext)

            return ComposingSelectableList(
                id,
                configure,
                ComposingSelectableListStyle(context, style),
                listContext
            ).addView()
        }
    }
}

class SelectableListConfig : ListConfig(), HasOnItemSelectedListener<SCV> {
    var selectableMode: SelectableMode = SelectableMode.SINGLE
    var isSelectEnabled: Boolean = true
    var isAllowToCancelSelection: Boolean = false

    override val selectedListeners: MutableList<OnItemSelectedListener<SCV>> = ArrayList()
}

open class ComposingSelectableListStyle(context: Context) : ComposingListStyle(context) {

    companion object {
        operator fun invoke(
            context: Context,
            init: ComposingSelectableListStyle.() -> Unit
        ): ComposingSelectableListStyle {
            return ComposingSelectableListStyle(context).apply(init)
        }
    }

    override val tag: String = "SelectableListStyle"
}