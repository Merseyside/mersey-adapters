package com.merseyside.adapters.core.base.callback

import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.IBaseAdapter

interface OnAttachToRecyclerViewListener {
    fun onAttached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>)

    fun onDetached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>)
}