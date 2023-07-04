package com.merseyside.adapters.compose.feature.placeholder

import com.merseyside.adapters.compose.view.base.SCV
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.DelegateAdapter
import com.merseyside.adapters.delegates.feature.placeholder.provider.PlaceholderProvider

class ComposePlaceholderProvider(
    private val provideView: () -> SCV
): PlaceholderProvider<SCV, VM<SCV>>() {

    override val placeholder: SCV
        get() = provideView()

    override val placeholderDelegate: DelegateAdapter<out SCV, SCV, out VM<SCV>>? = null
}