package com.merseyside.adapters.core.modelList.update

class UpdateBehaviour(
    val removeOld: Boolean = true,
    val addNew: Boolean = true
) {

    companion object {
        fun updateExistingOnly(): UpdateBehaviour {
            return UpdateBehaviour(removeOld = false, addNew = false)
        }
    }
}