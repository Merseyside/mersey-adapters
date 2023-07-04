package com.merseyside.adapters.delegates.feature.placeholder.provider

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.delegates.DelegateAdapter

abstract class PlaceholderProvider<Parent, ParentModel : VM<Parent>> {

    abstract val placeholder: Parent
    abstract val placeholderDelegate: DelegateAdapter<out Parent, Parent, out ParentModel>?
}