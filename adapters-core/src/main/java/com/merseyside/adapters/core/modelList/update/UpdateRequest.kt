package com.merseyside.adapters.core.modelList.update

class UpdateRequest<Item>(val list: List<Item>) {

    constructor(item: Item): this(listOf(item))

    var isAddNew = true
        private set

    var isDeleteOld = true
        private set

    class Builder<Item>(list: List<Item>) {

        constructor(item: Item): this(listOf(item))

        private val request: UpdateRequest<Item> = UpdateRequest(list)

        fun isAddNew(bool: Boolean): Builder<Item> {
            request.isAddNew = bool
            return this
        }

        fun isDeleteOld(bool: Boolean): Builder<Item> {
            request.isDeleteOld = bool
            return this
        }

        fun build(): UpdateRequest<Item> {
            return request
        }
    }

    companion object {
        fun <Item> fromBehaviour(items: List<Item>, updateBehaviour: UpdateBehaviour): UpdateRequest<Item> {
            updateBehaviour as UpdateBehaviour.UPDATE
            return Builder(items)
                .isAddNew(updateBehaviour.addNew)
                .isDeleteOld(updateBehaviour.removeOld)
                .build()
        }
    }
}

sealed class UpdateBehaviour {
    object ADD: UpdateBehaviour()
    open class UPDATE(val removeOld: Boolean = true, val addNew: Boolean = true): UpdateBehaviour()
    class ADD_UPDATE(removeOld: Boolean = true, addNew: Boolean = true): UPDATE(removeOld, addNew)
}