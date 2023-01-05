package com.merseyside.adapters.core.config.contract

import com.merseyside.adapters.core.config.update.UpdateActions
import com.merseyside.adapters.core.config.update.UpdateLogic
import com.merseyside.adapters.core.model.VM


interface UpdateLogicProvider<Parent, Model: VM<Parent>> {

    fun updateLogic(updateActions: UpdateActions<Parent, Model>): UpdateLogic<Parent, Model>
}