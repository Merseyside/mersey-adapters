package com.merseyside.adapters.sample.application.main.fragment.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.BR
import com.merseyside.archy.utils.ext.navigate
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.application.main.fragment.di.DaggerMainComponent
import com.merseyside.adapters.sample.application.main.fragment.di.MainModule
import com.merseyside.adapters.sample.application.main.fragment.model.MainViewModel
import com.merseyside.adapters.sample.databinding.FragmentMainBinding
import com.merseyside.utils.view.ext.onClick

class MainFragment : BaseSampleFragment<FragmentMainBinding, MainViewModel>() {

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.fragment_main
    override fun getTitle(context: Context): String = "Choose a feature"

    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerMainComponent.builder()
            .appComponent(appComponent)
            .mainModule(MainModule(this))
            .build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().run {
            newsButton.onClick { navigate(R.id.action_mainFragment_to_newsFragment) }
            colorsButton.onClick { navigate(R.id.action_mainFragment_to_colorsFragment) }
            racingButton.onClick { navigate(R.id.action_mainFragment_to_racingFragment) }
            concatButton.onClick { navigate(R.id.action_mainFragment_to_concatFragment) }
            contactsButton.onClick { navigate(R.id.action_mainFragment_to_contactsFragment) }
            delegatesButton.onClick { navigate(R.id.action_mainFragment_to_delegatesFragment) }
            composeButton.onClick { navigate(R.id.action_mainFragment_to_moviesFragment) }
        }
    }
}