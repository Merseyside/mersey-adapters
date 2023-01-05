package com.merseyside.adapters.core.base

import com.merseyside.adapters.core.model.VM

interface AdapterContract<Parent, Model : VM<Parent>> {

    val provideModelByItem: suspend (Parent) -> Model
}