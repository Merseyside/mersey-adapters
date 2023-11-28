package com.merseyside.adapters.compose.composer

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.merseyside.adapters.compose.adapter.AdapterComposer

abstract class FragmentAdapterComposer(fragment: Fragment) :
    AdapterComposer(fragment.requireActivity(), fragment, fragment.lifecycleScope)