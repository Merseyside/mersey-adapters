package com.merseyside.adapters.compose.view.text

import androidx.databinding.Bindable
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.view.base.model.ViewVM

open class ComposingTextViewModel<Item : ComposingText<*>>(
    item: Item
) : ViewVM<Item>(item) {

    override fun notifyUpdate() {
        super.notifyUpdate()
        notifyPropertyChanged(BR.text)
    }

    @Bindable
    fun getText(): String {
        return item.text
    }

    fun setText(value: String) {
        if (value != item.text) {
            item.text = value
        }
    }

}