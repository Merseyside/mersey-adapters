package com.merseyside.adapters.sample.application.base

import androidx.databinding.ViewDataBinding
import com.merseyside.adapters.sample.application.SampleApplication
import com.merseyside.archy.presentation.fragment.BaseVMFragment
import com.merseyside.archy.presentation.model.BaseViewModel

abstract class BaseSampleFragment<V: ViewDataBinding, M: BaseViewModel> : BaseVMFragment<V, M>() {

    protected val appComponent = SampleApplication.getInstance().appComponent
}