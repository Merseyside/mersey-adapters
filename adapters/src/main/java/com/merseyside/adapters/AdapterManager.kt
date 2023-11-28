package com.merseyside.adapters

import android.content.Context
import androidx.annotation.CallSuper
import androidx.core.view.doOnAttach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.base.BaseAdapter

abstract class AdapterManager<Key, Adapter> where Adapter : BaseAdapter<*, *> {

    var recyclerView: RecyclerView? = null
        private set

    private var recyclerLifecycleOwner: LifecycleOwner? = null
    private var lifecycleOwner: LifecycleOwner? = null

    private var adapterMap = mutableMapOf<Key, Adapter>()
    var currentKey: Key? = null
        private set
    private lateinit var currentAdapter: Adapter

    private var listener: OnAdapterChangedListener<Key, Adapter>? = null

    protected val context: Context
        get() = requireRecyclerView().context

    private val lifecycleObserver = object : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            super.onCreate(owner)
            this@AdapterManager.onCreate(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            this@AdapterManager.onDestroy(owner)
        }
    }

    private val recyclerLifecycleObserver = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            detachRecyclerView()
        }
    }

    abstract fun onCreate(lifecycleOwner: LifecycleOwner)
    abstract fun onDestroy(lifecycleOwner: LifecycleOwner)

    abstract fun createAdapter(key: Key, lifecycleOwner: LifecycleOwner): Adapter

    abstract fun onRecyclerAttached(
        recyclerView: RecyclerView,
        recyclerLifecycleOwner: LifecycleOwner
    )

    abstract fun onRecyclerDetached(
        recyclerView: RecyclerView,
        recyclerLifecycleOwner: LifecycleOwner
    )

    fun setAdaptersLifecycleOwner(lifecycleOwner: LifecycleOwner?) {
        this.lifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)
        this.lifecycleOwner = lifecycleOwner
        lifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
    }

    @CallSuper
    open fun setRecyclerView(recyclerView: RecyclerView?) {
        if (this.recyclerView == recyclerView) return

        if (this.recyclerView != null) {
            detachRecyclerView()
        }

        if (recyclerView != null) {
            attachRecyclerView(recyclerView)
        }
    }

    open fun setAdapterByKey(key: Key) {
        currentKey = key
        currentAdapter = getAdapterByKey(key).also { adapter ->
            requireRecyclerView().swapAdapter(adapter, false)
            listener?.onChanged(key, adapter)
        }
    }

    fun getAdapterByKey(key: Key): Adapter {
        return adapterMap[key] ?: createAdapter(key, requireLifecycleOwner()).also { adapter ->
            adapterMap[key] = adapter
        }
    }

    fun setOnAdapterChangedListener(listener: OnAdapterChangedListener<Key, Adapter>) {
        this.listener = listener
    }

    fun reset() {
        recyclerView?.adapter = null
        currentKey = null
        adapterMap.clear()
    }

    fun isEmpty(): Boolean {
        return adapterMap.isEmpty()
    }

    fun <R> runWithCurrentAdapter(block: Adapter.() -> R): R {
        return block(currentAdapter)
    }

    private fun attachRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        recyclerView.doOnAttach {
            this.recyclerLifecycleOwner = recyclerView.findViewTreeLifecycleOwner()
            if (lifecycleOwner == null) {
                setAdaptersLifecycleOwner(recyclerLifecycleOwner)
            }

            requireNotNull(recyclerLifecycleOwner).lifecycle.addObserver(recyclerLifecycleObserver)
            onRecyclerAttached(recyclerView, requireNotNull(recyclerLifecycleOwner))
        }
    }

    private fun detachRecyclerView() {
        onRecyclerDetached(requireNotNull(recyclerView), requireNotNull(recyclerLifecycleOwner))
        requireNotNull(recyclerLifecycleOwner).lifecycle.removeObserver(recyclerLifecycleObserver)
        recyclerView = null
        recyclerLifecycleOwner = null
    }

    private fun requireRecyclerView() = requireNotNull(recyclerView)

    private fun requireLifecycleOwner() = requireNotNull(lifecycleOwner)

    fun interface OnAdapterChangedListener<Key, Adapter : BaseAdapter<*, *>> {

        fun onChanged(key: Key, adapter: Adapter)
    }
}