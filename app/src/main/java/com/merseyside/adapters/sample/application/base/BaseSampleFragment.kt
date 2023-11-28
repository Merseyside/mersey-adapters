package com.merseyside.adapters.sample.application.base

import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.sample.application.SampleApplication
import com.merseyside.archy.presentation.fragment.BaseVMFragment
import com.merseyside.archy.presentation.model.BaseViewModel
import com.merseyside.archy.utils.ext.navigateUp

abstract class BaseSampleFragment<V: ViewDataBinding, M: BaseViewModel> : BaseVMFragment<V, M>() {

    protected val appComponent = SampleApplication.getInstance().appComponent

    override fun onNavigateUp() {
        navigateUp()
    }

    override fun isNavigateUpEnabled(): Boolean {
        return true
    }
}