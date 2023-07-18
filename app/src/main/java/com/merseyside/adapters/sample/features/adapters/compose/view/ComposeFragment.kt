package com.merseyside.adapters.sample.features.adapters.compose.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.adapters.compose.adapter.setAdapterComposer
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentComposeBinding
import com.merseyside.adapters.sample.features.adapters.compose.adapter.MovieScreenAdapterComposer
import com.merseyside.adapters.sample.features.adapters.compose.di.ComposeModule
import com.merseyside.adapters.sample.features.adapters.compose.di.DaggerComposeComponent
import com.merseyside.adapters.sample.features.adapters.compose.model.ComposeViewModel

class ComposeFragment : BaseSampleFragment<FragmentComposeBinding, ComposeViewModel>() {

    private val screenBuilder: MovieScreenAdapterComposer by lazy {
        MovieScreenAdapterComposer(this)
    }

    override fun getTitle(context: Context) = "ComposeScreen"
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.fragment_compose

    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerComposeComponent.builder()
            .appComponent(appComponent)
            .composeModule(ComposeModule(this))
            .build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().composite.setAdapterComposer(screenBuilder)
    }
}