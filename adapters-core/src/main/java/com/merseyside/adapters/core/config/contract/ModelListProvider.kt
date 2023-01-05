package com.merseyside.adapters.core.config.contract

import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.core.modelList.ModelList


interface ModelListProvider<Parent, Model: VM<Parent>> {
    val modelList: ModelList<Parent, Model>
}