package com.merseyside.adapters.core.holder

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.model.VM

abstract class ViewHolder<Parent, Model : VM<Parent>>(val root: View) : RecyclerView.ViewHolder(root) {

    lateinit var model: Model

    val isInitialized: Boolean
        get() = this::model.isInitialized

    val context: Context
        get() = itemView.context

    abstract fun bind(model: Model)
}