package com.merseyside.adapters.core.config.contract

import com.merseyside.adapters.core.workManager.AdapterWorkManager

interface HasAdapterWorkManager {

    val workManager: AdapterWorkManager
}