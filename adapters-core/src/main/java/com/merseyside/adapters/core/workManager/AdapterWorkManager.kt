package com.merseyside.adapters.core.workManager

import androidx.annotation.MainThread
import androidx.collection.ArraySet
import com.merseyside.adapters.core.async.runForUI
import com.merseyside.adapters.core.config.contract.HasAdapterWorkManager
import com.merseyside.merseyLib.kotlin.coroutines.queue.CoroutineQueue
import com.merseyside.merseyLib.kotlin.coroutines.queue.ext.executeAsync
import com.merseyside.merseyLib.kotlin.coroutines.utils.CompositeJob
import com.merseyside.merseyLib.kotlin.logger.Logger
import com.merseyside.merseyLib.kotlin.utils.ifTrue
import kotlinx.coroutines.Job
import java.util.Collections
import java.util.LinkedList
import kotlin.coroutines.CoroutineContext

class AdapterWorkManager(
    private val coroutineQueue: CoroutineQueue<Any, Unit>,
    private val coroutineContext: CoroutineContext,
    private val errorHandler: (Exception) -> Unit
) {

    private var parentWorkManager: AdapterWorkManager? = null
    private val subManagers = ArraySet<AdapterWorkManager>()

    private val onMainWorkStartedListeners = mutableListOf<OnMainWorkStartedListener>()
    internal val mainWorkList = Collections.synchronizedList<suspend () -> Unit>(LinkedList())

    private val hasQueueWork: Boolean
        get() = coroutineQueue.hasQueueWork

    fun <Result, T: HasAdapterWorkManager> subTaskWith(adapter: T, block: suspend T.() -> Result) {
        val subWorkManager = adapter.workManager
        subWorkManager.parentWorkManager = this
        subManagers.add(subWorkManager)
        subWorkManager.add { block(adapter) }
    }

    fun addOnMainWorkStartedListener(listener: OnMainWorkStartedListener) {
        onMainWorkStartedListeners.add(listener)
    }

    internal fun postMainWork(action: suspend () -> Unit) {
        mainWorkList.add(action)
    }

    fun <Result> doAsync(
        onComplete: (Result) -> Unit = {},
        onError: ((e: Exception) -> Unit)? = null,
        work: suspend () -> Result,
    ): Job? {
        add(onComplete, onError, work)
        return coroutineQueue.executeAsync(coroutineContext)
    }

    private fun executeAsync(): Job? {
        return coroutineQueue.executeAsync(coroutineContext)
    }

    private suspend fun runSubManagersAsyncWork() {
        if (subManagers.isNotEmpty()) {
            val subCompositeJob = CompositeJob()
            subManagers.forEach { manager ->
                if (manager.hasQueueWork) {
                    val job = manager.executeAsync()
                    if (job != null) subCompositeJob.add(job)
                }
            }

            subCompositeJob.joinAll()
        }
    }

    private suspend fun runMainWork() {
        notifyMainWorkStarted()
        while (mainWorkList.iterator().hasNext()) {
            val action = mainWorkList.removeFirst()
            action()
        }

        runSubManagersMainWork()
    }

    private suspend fun runSubManagersMainWork() {
        if (subManagers.isNotEmpty()) {
            subManagers.forEach { manager ->
                manager.runMainWork()
            }

            subManagers.clear()
        }
    }

    private fun <Result> add(
        onComplete: (Result) -> Unit = {},
        onError: ((e: Exception) -> Unit)? = null,
        work: suspend () -> Result
    ) {
        coroutineQueue.add {
            try {
                val result = work()
                runSubManagersAsyncWork()

                if (parentWorkManager == null) {
                    runForUI {
                        runMainWork()
                        onComplete(result)
                    }
                }
            } catch(e: Exception) {
                Logger.logErr("AdapterWorkManager", e)
                onError?.invoke(e) ?: errorHandler(e)
            }
        }
    }

    private suspend fun notifyMainWorkStarted() {
        onMainWorkStartedListeners.forEach { listener -> listener() }
    }

    fun cancel(): Boolean {
        return coroutineQueue.cancelAndClear().ifTrue {
            subManagers.clear()
            mainWorkList.clear()
        }
    }

    fun interface OnMainWorkStartedListener {
        @MainThread
        suspend operator fun invoke()
    }
}