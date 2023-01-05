package com.merseyside.adapters.coroutine

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.merseyside.adapters.core.base.BaseAdapter
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

fun <Parent> BaseAdapter<Parent, *>.setFlow(
    flow: Flow<Parent>,
    viewLifecycleOwner: LifecycleOwner,
    updateBehaviour: UpdateBehaviour = UpdateBehaviour.ADD_UPDATE()
) {
    setListFlow(flow.map { listOf(it) }, viewLifecycleOwner, updateBehaviour)
}

fun <Parent> BaseAdapter<Parent, *>.setListFlow(
    flow: Flow<List<Parent>>,
    viewLifecycleOwner: LifecycleOwner,
    updateBehaviour: UpdateBehaviour = UpdateBehaviour.ADD_UPDATE()
) {
    with(viewLifecycleOwner) {
        lifecycleScope.launch {
            flow.flowWithLifecycle(lifecycle).collect { items ->
                workManager.doAsync {
                    when (updateBehaviour) {
                        is UpdateBehaviour.ADD -> {
                            add(items)
                        }

                        else -> {
                            if (isEmpty()) {
                                if (updateBehaviour is UpdateBehaviour.ADD_UPDATE) {
                                    add(items)
                                    return@doAsync
                                }
                            }

                            update(UpdateRequest.fromBehaviour(items, updateBehaviour))
                        }
                    }
                }
            }
        }
    }
}