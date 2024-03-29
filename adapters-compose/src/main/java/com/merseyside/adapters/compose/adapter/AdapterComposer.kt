package com.merseyside.adapters.compose.adapter

import android.content.Context
import androidx.annotation.CallSuper
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.compose.dsl.context.ComposeContext
import com.merseyside.adapters.compose.dsl.context.compose
import com.merseyside.adapters.compose.model.ViewAdapterViewModel
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.async.doAsync
import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.base.callback.OnAttachToRecyclerViewListener
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.core.utils.InternalAdaptersApi
import kotlinx.coroutines.CoroutineScope
import java.lang.ref.WeakReference


abstract class AdapterComposer(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val coroutineScope: CoroutineScope
) {

    private val contextWeakReference = WeakReference(context)
    val context: Context
        get() = contextWeakReference.get() ?: throw NullPointerException("Already destroyed")

    private val lifecycleOwnerWeakReference = WeakReference(lifecycleOwner)
    val lifecycleOwner: LifecycleOwner
        get() = lifecycleOwnerWeakReference.get() ?: throw NullPointerException("Already destroyed")


    private lateinit var rootContext: ComposeContext
    private lateinit var compositeAdapter: SimpleViewCompositeAdapter

    @CallSuper
    open fun AdapterConfig<SCV, ViewAdapterViewModel>.configAdapter() {
        coroutineScope = this@AdapterComposer.coroutineScope
    }

    internal fun provideAdapter(): SimpleViewCompositeAdapter {
        if (!this::compositeAdapter.isInitialized) {
            createAdapter()
            onAdapterCreated(compositeAdapter)
        }

        return compositeAdapter
    }

    private fun createAdapter() {
        compositeAdapter = initAdapter(::SimpleViewCompositeAdapter) {
            configAdapter()
        }
    }

    @CallSuper
    @OptIn(InternalAdaptersApi::class)
    open fun onAdapterCreated(adapter: SimpleViewCompositeAdapter) {
        adapter.addOnAttachToRecyclerViewListener(object : OnAttachToRecyclerViewListener {
            override fun onAttached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                onRecyclerAttached(recyclerView, compositeAdapter)
            }

            override fun onDetached(recyclerView: RecyclerView, adapter: IBaseAdapter<*, *>) {
                onRecyclerDetached(recyclerView, compositeAdapter)
            }

        })
        invalidate()
    }

    open fun onRecyclerAttached(recyclerView: RecyclerView, adapter: SimpleViewCompositeAdapter) {
    }

    open fun onRecyclerDetached(recyclerView: RecyclerView, adapter: SimpleViewCompositeAdapter) {
    }

    @MainThread
    abstract fun ComposeContext.compose()

    private fun composeInternal() {
        rootContext = compose(context, lifecycleOwner, compositeAdapter) {
            compose()
        }
    }

    fun clear() {
        val onComplete: (Unit) -> Unit = { rootContext.clear() }
        compositeAdapter.doAsync(onComplete) {
            this@AdapterComposer.compositeAdapter.delegatesManager.resetDelegates()
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <View : SCV> findViewById(id: String): View? {
        return compositeAdapter.findViewById(id)
    }

    @InternalAdaptersApi
    fun invalidate() {
        runForUI {
            composeInternal()
        }
    }
}