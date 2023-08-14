package com.merseyside.adapters.compose.dsl.context

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.viewProvider.MutableComposeState
import com.merseyside.adapters.core.async.clearAsync
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.observable.Disposable
import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ext.compareAndUpdateNullable
import com.merseyside.merseyLib.kotlin.observable.ext.debounce
import com.merseyside.merseyLib.kotlin.observable.ext.merge
import com.merseyside.merseyLib.kotlin.observable.ext.mergeSingleEvent

abstract class ViewComposeContext<View : SCV>(
    contextId: String,
    val context: Context,
    val lifecycleOwner: LifecycleOwner,
    private val initContext: ViewComposeContext<View>.() -> Unit
) : ILogger, Identifiable<String> {

    override val id = contextId

    protected lateinit var relativeAdapter: ViewCompositeAdapter<SCV, VM<SCV>>
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
    open fun setRelativeAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        relativeAdapter = adapter
        onInitAdapter(adapter)
    }

    open fun onInitAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        invalidateContext()
    }

    abstract fun onViewsChanged(adapter: ViewCompositeAdapter<SCV, VM<SCV>>, data: List<View>)

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
            } else onViewsChanged(relativeAdapter, views)
        }
    }

    private fun stopObservingViews() {
        viewsObserverDisposable?.dispose()
    }

    fun getComposeState(propertyName: String): MutableComposeState<*>? {
        return composeStates.find { it.propertyName == propertyName }
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
            .debounce(50L)
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