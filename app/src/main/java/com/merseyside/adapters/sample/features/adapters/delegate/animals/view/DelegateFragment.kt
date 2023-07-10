package com.merseyside.adapters.sample.features.adapters.delegate.animals.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.base.callback.onClick
import com.merseyside.adapters.core.feature.sorting.Sorting
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentDelegateBinding
import com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter.AnimalsAdapter
import com.merseyside.adapters.sample.features.adapters.delegate.animals.adapter.AnimalsComparator
import com.merseyside.adapters.sample.features.adapters.delegate.animals.di.DelegateModule
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Animal
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Cat
import com.merseyside.adapters.sample.features.adapters.delegate.animals.entity.Dog
import com.merseyside.adapters.sample.features.adapters.delegate.animals.model.DelegateViewModel
import com.merseyside.adapters.sample.features.adapters.delegate.animals.di.DaggerDelegateComponent
import com.merseyside.merseyLib.kotlin.logger.log
import com.merseyside.utils.view.ext.onClick
import com.merseyside.utils.delayedMainThread
import com.merseyside.merseyLib.time.units.Seconds

class DelegateFragment : BaseSampleFragment<FragmentDelegateBinding, DelegateViewModel>() {

    private val animalsAdapter = AnimalsAdapter {
        Sorting {
            comparator = AnimalsComparator()
        }
    }.apply { onClick { showMsg("Clicked!") }}

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

        with(requireBinding().recycler) {
            this.adapter = animalsAdapter
        }
        animalsAdapter.addAsync(Cat("Squirty", 5, "abc"))

        requireBinding().populate.onClick {
            animalsAdapter.updateAsync(getData())
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