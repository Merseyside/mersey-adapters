package com.merseyside.adapters.core.async

import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.update.UpdateRequest

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    item: Parent,
    onComplete: (Model?) -> Unit = {}
) {
    workManager.doAsync(onComplete) { add(item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    items: List<Parent>,
    onComplete: (Unit) -> Unit = {}
) {
    workManager.doAsync(onComplete) { add(items) }
}

fun <Parent> IBaseAdapter<Parent, *>.addOrUpdateAsync(
    items: List<Parent>,
    onComplete: (Unit) -> Unit = {}
) {
    workManager.doAsync(onComplete) { addOrUpdate(items) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.updateAsync(
    updateRequest: UpdateRequest<Parent>,
    provideResult: (Boolean) -> Unit = {}
) {
    workManager.doAsync(provideResult) { update(updateRequest) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.updateAsync(
    items: List<Parent>,
    onComplete: (Boolean) -> Unit = {}
) {
    workManager.doAsync(onComplete) { update(items) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.removeAsync(
    item: Parent,
    onComplete: (Model?) -> Unit = {}
) {
    workManager.doAsync(onComplete) { remove(item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.removeAsync(
    items: List<Parent>,
    onComplete: (List<Model>) -> Unit = {}
) {
    workManager.doAsync(onComplete) { remove(items) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.getModelByItemAsync(
    item: Parent,
    onComplete: (Model?) -> Unit
) {
    workManager.doAsync(onComplete) { getModelByItem(item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.clearAsync(
    onComplete: (Unit) -> Unit = {}
) {
    workManager.doAsync(onComplete, work = ::clear)
}


/* Position */

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    position: Int,
    item: Parent,
    onComplete: (Unit) -> Unit
) {
    workManager.doAsync(onComplete) { add(position, item) }
}

fun <Parent, Model : VM<Parent>> IBaseAdapter<Parent, Model>.addAsync(
    position: Int,
    items: List<Parent>,
    onComplete: (Unit) -> Unit
) {
    workManager.doAsync(onComplete) { add(position, items) }
}