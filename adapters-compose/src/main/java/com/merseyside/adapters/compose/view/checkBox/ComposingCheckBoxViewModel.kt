package com.merseyside.adapters.compose.view.checkBox

import com.merseyside.adapters.compose.view.text.ComposingTextViewModel
import com.merseyside.adapters.core.feature.selecting.SelectState
import com.merseyside.adapters.core.feature.selecting.SelectableItem
import com.merseyside.merseyLib.kotlin.logger.ILogger

class ComposingCheckBoxViewModel(
    item: CheckBox,
    override val selectState: SelectState = SelectState(item.checked)
) : ComposingTextViewModel<CheckBox>(item), SelectableItem, ILogger {

    override val tag: String = "ComposingCheckBox"
}