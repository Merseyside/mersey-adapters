package com.merseyside.adapters.compose.view.image

import android.content.Context
import android.widget.ImageView
import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.compose.BR
import com.merseyside.adapters.compose.R
import com.merseyside.adapters.compose.delegate.ViewDelegateAdapter

class ComposingImageDelegate<View : ComposingImage<Style>, Style : ComposingImageStyle, VM : ComposingImageViewModel<View>> :
    ViewDelegateAdapter<View, Style, VM>() {

    override fun getLayoutIdForItem(viewType: Int) = R.layout.view_composing_image

    override fun getBindingVariable() = BR.model

    @Suppress("UNCHECKED_CAST")
    override fun createItemViewModel(item: View) = ComposingImageViewModel(item) as VM

    override fun applyStyle(context: Context, viewDataBinding: ViewDataBinding, style: Style) {
        super.applyStyle(context, viewDataBinding, style)
        val image = viewDataBinding.root as ImageView

        with(image) {
            adjustViewBounds = style.adjustViewBounds
            scaleType = style.scaleType
        }
    }
}