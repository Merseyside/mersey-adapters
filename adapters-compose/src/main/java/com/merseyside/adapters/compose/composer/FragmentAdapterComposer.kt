package com.merseyside.adapters.compose.composer

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import com.merseyside.adapters.compose.adapter.HasCompositeAdapter

abstract class FragmentAdapterComposer(private val fragment: Fragment): HasCompositeAdapter {

    override val context: Context
        get() = fragment.requireContext()

    override val viewLifecycleOwner: LifecycleOwner
        get() = fragment.viewLifecycleOwner
}