package com.merseyside.adapters.sample.features.adapters.concat.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.ConcatAdapter
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentConcatAdapterBinding
import com.merseyside.adapters.sample.features.adapters.concat.adapter.AdsAdapter
import com.merseyside.adapters.sample.features.adapters.concat.adapter.NewsAdapter
import com.merseyside.adapters.sample.features.adapters.concat.di.ConcatModule
import com.merseyside.adapters.sample.features.adapters.concat.di.DaggerConcatComponent
import com.merseyside.adapters.sample.features.adapters.concat.model.ConcatViewModel

class ConcatAdapterFragment : BaseSampleFragment<FragmentConcatAdapterBinding, ConcatViewModel>() {

    private val concatAdapter = ConcatAdapter()
    private val newsAdapter = NewsAdapter()
    private val adsAdapter = AdsAdapter()

    override fun getLayoutId() = R.layout.fragment_concat_adapter
    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerConcatComponent.builder()
            .appComponent(appComponent)
            .concatModule(ConcatModule(this))
            .build().inject(this)
    }

    override fun getTitle(context: Context) = "Concat"
    override fun getBindingVariable() = BR.viewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        concatAdapter.addAdapter(newsAdapter)
        concatAdapter.addAdapter(adsAdapter)

        requireBinding().recycler.adapter = concatAdapter

        viewModel.getNewsFlow().asLiveData().observe(viewLifecycleOwner) {
            newsAdapter.addAsync(it)
        }

        viewModel.getAdsFlow().asLiveData().observe(viewLifecycleOwner) {
            adsAdapter.addAsync(it)
        }
    }

    override fun onResume() {
        super.onResume()

        viewModel.startProducers()
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopProducer()
    }
}