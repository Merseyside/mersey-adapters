package com.merseyside.adapters.compose.delegate

import android.content.Context
import androidx.annotation.CallSuper
import androidx.core.view.updateLayoutParams
import com.merseyside.adapters.compose.style.ComposingStyle
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.view.base.StyleableComposingView
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.merseyLib.kotlin.utils.safeLet
import com.merseyside.utils.ext.setMarginsRes
import com.merseyside.utils.view.ext.padding

abstract class ViewDelegateAdapter<View : StyleableComposingView<Style>, Style : ComposingStyle, Model>
    : DelegateAdapter<View, SCV, Model>()
        where Model : AdapterParentViewModel<View, SCV> {

    @CallSuper
    open fun applyStyle(context: Context, holder: ViewHolder<SCV, Model>, style: Style) {
        with(style) {
            val view = holder.root
            view.updateLayoutParams {
                safeLet(style.width) { width = it }
                safeLet(style.height) { height = it }

                margins?.run {
                    setMarginsRes(context, start, top, end, bottom)
                }
            }

            safeLet(style.elevation) {
                view.elevation = it
            }

            safeLet(backgroundColor) { color ->
                view.setBackgroundColor(color)
            }

            safeLet(paddings) { paddings ->
                with(paddings) {
                    view.padding(start, top, end, bottom)
                }
            }
        }
    }

    @InternalAdaptersApi
    override fun onModelCreated(model: Model) {
        super.onModelCreated(model)
        model.addOnClickListener { item -> model.item.notifyOnClick(item) }
    }

    override fun onBindViewHolder(holder: ViewHolder<SCV, Model>, model: Model, position: Int) {
        super.onBindViewHolder(holder, model, position)
        applyStyle(holder.context, holder, model.item.composingStyle)
    }

    override fun onBindViewHolder(
        holder: ViewHolder<SCV, Model>,
        model: Model,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, model, position, payloads)
        applyStyle(holder.context, holder, model.item.composingStyle)
    }
}

typealias ViewDelegate<Style> = ViewDelegateAdapter<out StyleableComposingView<Style>, Style,
        out AdapterParentViewModel<out StyleableComposingView<Style>, SCV>>