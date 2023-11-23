package com.merseyside.adapters.compose.dsl.context

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.SimpleViewCompositeAdapter
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.viewProvider.MutableComposeState
import com.merseyside.adapters.core.async.clearAsync
import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.workManager.AdapterWorkManager
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.observable.Disposable
import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndUpdateNullable
import com.merseyside.merseyLib.kotlin.observable.ext.debounce
import com.merseyside.merseyLib.kotlin.observable.ext.mergeSingleEvent
import java.lang.ref.WeakReference

abstract class ViewComposeContext<View : SCV>(
    contextId: String,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    private val initContext: ViewComposeContext<View>.() -> Unit
) : HasAdapterWorkManager, Identifiable<String>, ILogger  {

    override val id = contextId

    private val contextWeakReference = WeakReference(context)
    private val lifecycleOwnerWeakReference = WeakReference(lifecycleOwner)

    override val workManager: AdapterWorkManager
        get() = relativeAdapter.workManager

    val context: Context
        get() = contextWeakReference.get() ?: throw NullPointerException("Already destroyed")
    val lifecycleOwner: LifecycleOwner
        get() = lifecycleOwnerWeakReference.get() ?: throw NullPointerException("Already destroyed")

    lateinit var relativeAdapter: ViewCompositeAdapter<SCV, VM<SCV>>
        private set
    private val childContextList = HashMap<String, ComposeContext>()
    private val composeStates: MutableSet<MutableComposeState<*>> = mutableSetOf()

    private val mutViews = MutableObservableField<List<View>?>(null)

    private var viewsObserverDisposable: Disposable<List<View>?>? = null

    private var composeStatesDisposable: Disposable<Unit>? = null

    @Suppress("UNCHECKED_CAST")
    fun <Cont : ComposeContext> getOrCreateChildContext(
        contextId: String,
        init: (contextId: String, context: Context, lifecycleOwner: LifecycleOwner) -> Cont
    ): Cont {
        val existContext = childContextList[contextId] as? Cont
        return existContext ?: init(contextId, context, lifecycleOwner).also {
            childContextList[contextId] = it
        }
    }

    fun updateViews(update: (current: List<View>) -> List<View>): Boolean {
        return mutViews.compareAndUpdateNullable { views ->
            update(views ?: emptyList())
        }
    }

    @CallSuper
    open fun setRelativeAdapter(adapter: SimpleViewCompositeAdapter) {
        relativeAdapter = adapter
        runForUI { onInitAdapter(adapter) }
    }

    open fun onInitAdapter(adapter: SimpleViewCompositeAdapter) {
        invalidateContext()
    }

    abstract fun onViewsChanged(adapter: SimpleViewCompositeAdapter, data: List<View>)

    /**
     * Calls when state changes. Usually when declared compose state handle new value.
     * By default clear all views and reinitialize context.
     */
    open fun onContextStateChanged() {
        invalidateContext()
    }

    private fun startObservingViews() {
        viewsObserverDisposable = mutViews.observe { views ->
            if (views.isNullOrEmpty()) {
                mutViews.value = null
                relativeAdapter.clearAsync()
            } else onViewsChanged(relativeAdapter, views).log("on views changed")
        }
    }

    private fun stopObservingViews() {
        viewsObserverDisposable?.dispose()
    }

    fun getComposeState(propertyName: String): MutableComposeState<*>? {
        return composeStates.find { state -> state.propertyName == propertyName }
    }

    protected fun clearViews() {
        mutViews.value = emptyList()
    }

    protected fun invalidateContext() {
        mutableState {
            clearViews()
            initContext()
            observeComposeStates()
        }
    }

    open fun clear() {
        childContextList.forEach { (_, context) -> context.clear() }
        childContextList.clear()
        clearViews()
        clearComposeStates()
    }

    private fun clearComposeStates() {
        composeStates.forEach { state -> state.close() }
        composeStates.clear()
    }

    protected fun mutableState(block: () -> Unit) {
        stopObservingViews()
        block()
        startObservingViews()
    }

    fun getViews(): List<View> {
        return mutViews.value ?: emptyList()
    }

    private fun observeComposeStates() {
        composeStatesDisposable?.dispose()
        composeStatesDisposable = mergeSingleEvent(composeStates.map { it.onChangeEvent })
            .debounce(10L)
            .observe { onContextStateChanged() }
    }

    internal fun addComposeState(state: MutableComposeState<*>) {
        composeStates.add(state)
    }

    override val tag: String = contextId
}

context(ComposeContext)
fun <View : SCV> View.addView(): View {
    updateViews { current -> current.plus(this) }
    return this
}

typealias ComposeContext = ViewComposeContext<SCV>