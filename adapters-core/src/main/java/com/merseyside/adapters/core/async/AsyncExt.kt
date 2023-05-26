package com.merseyside.adapters.core.async

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour
import kotlinx.coroutines.Job

fun <Parent, Model : VM<Parent>, Result> IBaseAdapter<Parent, Model>.doAsync(
    onComplete: (Result) -> Unit = {},
    onError: ((e: Exception) -> Unit)? = null,
    work: suspend IBaseAdapter<Parent, Model>.() -> Result,
): Job? {
    return workManager.doAsync(onComplete, onError) { work() }
}

fun <Parent> IBaseAdapter<Parent, *>.addAsync(
    item: Parent,
    onComplete: (Unit) -> Unit = {}
) {
    doAsync(onComplete) { add(item) }
}

fun <Parent> IBaseAdapter<Parent, *>.addAsync(
    items: List<Parent>,
    onComplete: (Unit) -> Unit = {}
) {
    doAsync(onComplete) { add(items) }
}

fun <Parent> IBaseAdapter<Parent, *>.addAsyncToStart(
    item: Parent,
    onComplete: (Unit) -> Unit = {}
) {
    addAsync(position = 0, item, onComplete)
}

fun <Parent> IBaseAdapter<Parent, *>.addAsyncToStart(
    items: List<Parent>,
    onComplete: (Unit) -> Unit = {}
) {
    addAsync(position = 0, items, onComplete)
}

fun <Parent> IBaseAdapter<Parent, *>.updateAsync(
    items: List<Parent>,
    updateBehaviour: UpdateBehaviour = UpdateBehaviour(),
    onComplete: (Boolean) -> Unit = {}
) {
    doAsync(onComplete) { update(items, updateBehaviour) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.removeAsync(
    item: Parent,
    onComplete: (Model?) -> Unit = {}
) {
    doAsync(onComplete) { remove(item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.removeAsync(
    items: List<Parent>,
    onComplete: (List<Model>) -> Unit = {}
) {
    doAsync(onComplete) { remove(items) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.getModelByItemAsync(
    item: Parent,
    onComplete: (Model?) -> Unit = {}
) {
    doAsync(onComplete) { getModelByItem(item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.clearAsync(
    onComplete: (Unit) -> Unit = {}
) {
    doAsync(onComplete) { clear() }
}


/* Position */

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    position: Int,
    item: Parent,
    onComplete: (Unit) -> Unit = {}
) {
    doAsync(onComplete) { add(position, item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    position: Int,
    items: List<Parent>,
    onComplete: (Unit) -> Unit = {}
) {
    doAsync(onComplete) { add(position, items) }
}