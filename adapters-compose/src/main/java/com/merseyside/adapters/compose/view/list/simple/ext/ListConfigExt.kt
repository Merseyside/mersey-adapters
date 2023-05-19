package com.merseyside.adapters.compose.view.list.simple.ext

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.view.list.simple.ListConfig
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener

fun ListConfig.onRecyclerAttached(onAttached: (recyclerView: RecyclerView) -> Unit) {
    addOnAttachToRecyclerViewListener(object : OnAttachToRecyclerViewListener {
        override fun onAttached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>) {
            onAttached(recyclerView)
        }
        override fun onDetached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>) {}
    })
}