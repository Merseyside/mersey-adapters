package com.merseyside.adapters.compose.view.image

import android.content.Context
import android.widget.ImageView
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.holder.ViewHolder

class ComposingImageDelegate<View : ComposingImage<Style>, Style : ComposingImageStyle, Model : ComposingImageViewModel<View>> :
    ViewDelegateAdapter<View, Style, Model>() {

    override fun getLayoutIdForItem() = R.layout.view_composing_image

    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingImageViewModel(item) as Model

    override fun applyStyle(context: Context, holder: ViewHolder<SCV, Model>, style: Style) {
        super.applyStyle(context, holder, style)
        val image = holder.root as ImageView

        with(image) {
            adjustViewBounds = style.adjustViewBounds
            scaleType = style.scaleType
        }
    }
}