package com.merseyside.adapters.core.modelList.update

sealed class UpdateBehaviour {
    object ADD: UpdateBehaviour()
    open class UPDATE(val removeOld: Boolean = true, val addNew: Boolean = true): UpdateBehaviour()
    class ADD_UPDATE(removeOld: Boolean = true, addNew: Boolean = true): UPDATE(removeOld, addNew)
}