package com.merseyside.adapters.sample.application.main.activity

import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.merseyside.adapters.sample.databinding.ActivityMainBinding
import com.merseyside.adapters.sample.R
import com.merseyside.archy.presentation.activity.BaseBindingActivity

class MainActivity : BaseBindingActivity<ActivityMainBinding>() {

    override fun performInjection(bundle: Bundle?, vararg params: Any) {}
    override fun getLayoutId() = R.layout.activity_main
    override fun getMainToolbar(): Toolbar = requireBinding().toolbar
    override fun getFragmentContainer() = R.id.nav_host_fragment
}