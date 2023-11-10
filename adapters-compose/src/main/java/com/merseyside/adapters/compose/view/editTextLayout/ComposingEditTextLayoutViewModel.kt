package com.merseyside.adapters.compose.view.editTextLayout

import androidx.databinding.Bindable
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.view.base.model.ViewVM

open class ComposingEditTextLayoutViewModel<Item : ComposingEditTextLayout<*>>(
    item: Item
) : ViewVM<Item>(item) {

    override suspend fun onUpdate() {
        notifyPropertyChanged(BR.text)
        notifyPropertyChanged(BR.clearFocus)
    }

    @Bindable
    fun getText() = item.text

    @Bindable
    fun isClearFocus() = item.isClearFocus

    fun onTextChanged(text: CharSequence) {
        item.textWatcherCallback.invoke(text.toString())
    }

}