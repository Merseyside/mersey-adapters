package com.merseyside.adapters.compose.composer

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.adapter.HasCompositeAdapter
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.merseyLib.kotlin.logger.log

abstract class FragmentAdapterComposer(
    protected val fragment: Fragment,
    final override val adapter: ViewCompositeAdapter<SCV, ViewAdapterViewModel>
) : HasCompositeAdapter {

    override lateinit var rootContext: ComposeContext

    init {
        adapter.addOnAttachToRecyclerViewListener(object : OnAttachToRecyclerViewListener {
            override fun onAttached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                invalidateAsync()
            }

            override fun onDetached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                clear()
            }
        })
    }

    override val context: Context
        get() = fragment.requireActivity()

    override val viewLifecycleOwner: LifecycleOwner
        get() = fragment
}