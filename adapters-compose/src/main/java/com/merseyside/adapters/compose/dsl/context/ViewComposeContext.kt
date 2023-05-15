package com.merseyside.adapters.compose.dsl.context

import android.content.Context
import androidx.annotation.CallSuper
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.ViewCompositeAdapter
import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.compose.viewProvider.MutableComposeState
import com.merseyside.adapters.core.async.clearAsync
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.model.VM
import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.kotlin.logger.ILogger
import com.merseyside.merseyLib.kotlin.observable.Disposable
import com.merseyside.merseyLib.kotlin.observable.MutableObservableField
import com.merseyside.merseyLib.kotlin.observable.ext.updateNotNull
import com.merseyside.merseyLib.kotlin.observable.ext.valueNotNull
import com.merseyside.merseyLib.kotlin.utils.safeLet


abstract class ViewComposeContext<View : SCV>(
    private val contextId: String,
    val context: Context,
    val viewLifecycleOwner: LifecycleOwner,
    internal val initContext: ViewComposeContext<View>.() -> Unit
) : ILogger, Identifiable<String> {

    protected lateinit var relativeAdapter: ViewCompositeAdapter<SCV, VM<SCV>>
        private set
    private val childContextList = HashMap<String, ComposeContext>()
    private val composeStates: MutableList<MutableComposeState<*>> = ArrayList()

    private val mutViews = MutableObservableField<List<View>>(emptyList())

    private var viewsObserverDisposable: Disposable<List<View>>? = null

    @Suppress("UNCHECKED_CAST")
    fun <Cont : ComposeContext> getOrCreateChildContext(
        contextId: String,
        init: (contextId: String, context: Context, viewLifecycleOwner: LifecycleOwner) -> Cont
    ): Cont {
        val existContext = childContextList[contextId] as? Cont
        return existContext ?: init(contextId, context, viewLifecycleOwner).also {
            childContextList[contextId] = it
        }
    }

    fun updateViews(update: (current: List<View>) -> List<View>) {
        mutViews.updateNotNull(update)
    }

    @CallSuper
    open fun setRelativeAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        "set relative adapter".log("kek")
        relativeAdapter = adapter
        onInitAdapter(adapter)
    }

    open fun onInitAdapter(adapter: ViewCompositeAdapter<SCV, VM<SCV>>) {
        initContext()
        startObservingViews()
    }

    abstract fun onNewData(data: List<View>)

    /**
     * Calls when state changes. Usually when declared compose state handle new value.
     * By default clear all views and reinitialize context.
     */
    abstract fun onContextStateChanged()

    private fun startObservingViews() {
        viewsObserverDisposable = mutViews.observe(ignoreCurrent = false) { views ->
            safeLet(views) { v ->
                if (v.isEmpty()) relativeAdapter.clearAsync()
                else onNewData(v)
            }
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

    protected fun mutableState(block: () -> Unit) {
        stopObservingViews()
        block()
        startObservingViews()
    }

    fun getViews(): List<View> {
        return mutViews.valueNotNull()
    }

    override fun getId() = contextId

    internal fun addComposeState(state: MutableComposeState<*>) {
        composeStates.add(state)
        state.observe {
            mutableState {
                onContextStateChanged()
            }
        }
    }

    override val tag: String = "ScreenComposeContext"
}

context(ComposeContext)
fun <View : SCV> View.addView(): View {
    updateViews { current -> current.plus(this) }
    return this
}

typealias ComposeContext = ViewComposeContext<SCV>