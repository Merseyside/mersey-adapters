package com.merseyside.adapters.sample.features.adapters.racers.view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.core.feature.positioning.Positioning
import com.merseyside.adapters.core.feature.sorting.Sorting
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour
import com.merseyside.adapters.coroutine.setFlow
import com.merseyside.adapters.decorator.SimpleItemOffsetDecorator
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentRacingBinding
import com.merseyside.adapters.sample.features.adapters.racers.adapter.RacersAdapter
import com.merseyside.adapters.sample.features.adapters.racers.adapter.RacersComparator
import com.merseyside.adapters.sample.features.adapters.racers.di.DaggerRacingComponent
import com.merseyside.adapters.sample.features.adapters.racers.di.RacingModule
import com.merseyside.adapters.sample.features.adapters.racers.model.RacingViewModel

class RacingFragment : BaseSampleFragment<FragmentRacingBinding, RacingViewModel>() {

    private val adapter = initAdapter(::RacersAdapter) {
        Sorting {
            comparator = RacersComparator
        }

        Positioning()
    }

    override fun hasTitleBackButton() = true
    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.fragment_racing
    override fun getTitle(context: Context) = "Racing"

    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerRacingComponent.builder()
            .appComponent(appComponent)
            .racingModule(RacingModule(this))
            .build().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().racersList.apply {
            adapter = this@RacingFragment.adapter
            addItemDecoration(
                SimpleItemOffsetDecorator(
                    context,
                    R.dimen.small_spacing,
                    R.dimen.normal_spacing
                )
            )
        }

        adapter.setFlow(
            flow = viewModel.getCheckpointFlow(),
            viewLifecycleOwner = viewLifecycleOwner,
            updateBehaviour = UpdateBehaviour.ADD_UPDATE(removeOld = false)
        )
    }
}