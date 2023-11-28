package com.merseyside.adapters.core.modelList.callback

fun interface OnModelListChangedCallback {

    suspend fun onModelListChanged(oldSize: Int, newSize: Int, hasChanges: Boolean)
}