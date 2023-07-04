package com.merseyside.adapters.core.modelList.update

class UpdateRequest<Item>(val items: List<Item>) {

    constructor(item: Item): this(listOf(item))

    var addNew = true
        private set

    var removeOld = true
        private set

    internal fun isUpdateExistingOnly(): Boolean {
        return !removeOld && !addNew
    }

    internal fun isFullUpdate(): Boolean {
        return removeOld && addNew
    }

    class Builder<Item>(list: List<Item>) {

        constructor(item: Item): this(listOf(item))

        private val request: UpdateRequest<Item> = UpdateRequest(list)

        fun isAddNew(bool: Boolean): Builder<Item> {
            request.addNew = bool
            return this
        }

        fun isDeleteOld(bool: Boolean): Builder<Item> {
            request.removeOld = bool
            return this
        }

        fun build(): UpdateRequest<Item> {
            return request
        }
    }

    companion object {
        fun <Item> fromBehaviour(items: List<Item>, updateBehaviour: UpdateBehaviour): UpdateRequest<Item> {
            return Builder(items)
                .isAddNew(updateBehaviour.addNew)
                .isDeleteOld(updateBehaviour.removeOld)
                .build()
        }
    }
}