package com.merseyside.adapters.core.feature.dataProvider

import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.addAsyncToStart
import com.merseyside.adapters.core.base.IBaseAdapter

/**
 * Works in pair with [DataProvider]
 * Observes data from [DataProvider]
 * Simply add data to start or end of the
 * @param addToStart set's behaviour of adding
 */
class AddDataObserver<Item: Parent, Parent>(
    private val addToStart: Boolean = false
) : DataObserver<Item, Parent> {

    override fun onDataProvided(adapter: IBaseAdapter<Parent, *>, data: Item) {
        val newData = castToAdaptersList(data)
        if (!addToStart) adapter.addAsync(newData)
        else adapter.addAsyncToStart(newData)
    }

    @Suppress("UNCHECKED_CAST")
    private fun castToAdaptersList(data: Item): List<Parent> {
        return if (data is List<*>) {
            data as List<Parent>
        } else listOf(data as Parent)
    }
}