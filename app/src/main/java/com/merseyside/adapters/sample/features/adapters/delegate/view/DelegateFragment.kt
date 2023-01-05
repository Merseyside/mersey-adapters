package com.merseyside.adapters.sample.features.adapters.delegate.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.base.callback.onClick
import com.merseyside.adapters.core.feature.sorting.Sorting
import com.merseyside.adapters.core.modelList.update.UpdateRequest
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentDelegateBinding
import com.merseyside.adapters.sample.features.adapters.delegate.adapter.AnimalsAdapter
import com.merseyside.adapters.sample.features.adapters.delegate.adapter.AnimalsComparator
import com.merseyside.adapters.sample.features.adapters.delegate.di.DaggerDelegateComponent
import com.merseyside.adapters.sample.features.adapters.delegate.di.DelegateModule
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Cat
import com.merseyside.adapters.sample.features.adapters.delegate.entity.Dog
import com.merseyside.adapters.sample.features.adapters.delegate.model.DelegateViewModel
import com.merseyside.utils.view.ext.onClick

class DelegateFragment : BaseSampleFragment<FragmentDelegateBinding, DelegateViewModel>() {

    private val adapter = AnimalsAdapter {
        Sorting {
            comparator = AnimalsComparator()
        }
    }.apply { onClick { showMsg("Clicked!") } }

    override fun hasTitleBackButton() = true
    override fun getLayoutId() = R.layout.fragment_delegate
    override fun getTitle(context: Context) = "Delegate"
    override fun getBindingVariable() = BR.viewModel

    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerDelegateComponent.builder()
            .appComponent(appComponent)
            .delegateModule(getDelegateModule())
            .build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().recycler.adapter = adapter
        adapter.addAsync(Cat("Squirty", 5, "abc"))

        requireBinding().populate.onClick {
            adapter.updateAsync(UpdateRequest.Builder(getData()).build())
        }
    }

    private fun getData(): List<Animal> {
        return listOf(
            Cat("Squirty", 5, "abc"),
            Cat("Mary", 12, "def"),
            Dog("Woof", 1, "ghi")
        )
    }

    private fun getDelegateModule(): DelegateModule {
        return DelegateModule(this)
    }
}