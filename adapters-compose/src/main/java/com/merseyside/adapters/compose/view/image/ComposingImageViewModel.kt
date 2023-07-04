package com.merseyside.adapters.compose.view.image

import android.graphics.drawable.Drawable
import androidx.databinding.Bindable
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.view.base.model.ViewVM

class ComposingImageViewModel<Item : ComposingImage<*>>(item: Item) : ViewVM<Item>(item) {

    @Bindable
    fun getDrawable(): Drawable {
        return item.drawable
    }

    override fun notifyUpdate() {
        super.notifyUpdate()
        notifyPropertyChanged(BR.drawable)
    }
}