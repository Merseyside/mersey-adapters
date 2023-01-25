package com.merseyside.adapters.core.inflater

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.children

class AsyncFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(
    context,
    attrs,
    defStyleAttr,
    defStyleRes
) {
    init {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    private var isInflated = false
    private var pendingActions: MutableList<AsyncFrameLayout.(View) -> Unit> = ArrayList()

    fun inflateAsync(layoutResId: Int) {
        AsyncLayoutInflater(context).inflate(layoutResId, this) { view, _, _ ->
            addView(view)
            isInflated = true
            pendingActions.forEach { action -> action(view) }
            pendingActions.clear()
        }
    }

    fun invokeWhenInflated(action: AsyncFrameLayout.(View) -> Unit) {
        if (isInflated) {
            action(children.first())
        } else {
            pendingActions.add(action)
        }
    }
}