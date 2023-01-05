package com.merseyside.adapters.core.base

import com.merseyside.adapters.core.model.VM


interface AdapterActions<Parent, Model : VM<Parent>> :
    AdapterContract<Parent, Model>