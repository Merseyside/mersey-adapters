package com.merseyside.adapters.core.feature.dataProvider

import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.base.IBaseAdapter
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour

/**
 * Works in pair with [DataProvider]
 * Observes data from [DataProvider]
 * Simply updates adapter with provided data
 * @param updateBehaviour set's behaviour of updating
 */
open class UpdateDataObserver<Item : Parent, Parent>(
    private val updateBehaviour: UpdateBehaviour = UpdateBehaviour()
) : DataObserver<Item, Parent> {

    constructor(
        removeOld: Boolean = true,
        addNew: Boolean = true
    ): this(UpdateBehaviour(removeOld, addNew))

    override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: Item) {
        adapter.updateAsync(castToAdaptersList(data), updateBehaviour)
    }

    @Suppress("UNCHECKED_CAST")
    private fun castToAdaptersList(data: Item): List<Parent> {
        return data as List<Parent>
    }
}