package com.merseyside.adapters.core.base.callback

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter

interface OnAttachToRecyclerViewListener {
    fun onAttached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>)

    fun onDetached(recyclerView: RecyclerView, adapter: BaseAdapter<*, *>)
}