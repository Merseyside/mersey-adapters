package com.merseyside.adapters.compose.view.editTextLayout

import androidx.databinding.Bindable
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.view.base.model.ViewVM

class ComposingEditTextLayoutViewModel<Item : ComposingEditTextLayout<*>>(
    item: Item
) : ViewVM<Item>(item) {

    override fun notifyUpdate() {
        notifyPropertyChanged(BR.text)
        notifyPropertyChanged(BR.clearFocus)
        notifyPropertyChanged(BR.hintText)
    }

    @Bindable
    fun getText() = item.text

    @Bindable
    fun getHintText() = item.hintText

    @Bindable
    fun isClearFocus() = item.isClearFocus

    fun onTextChanged(text: CharSequence) {
        item.textWatcherCallback.invoke(text.toString())
    }

}