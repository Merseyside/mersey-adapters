package com.merseyside.adapters.sample.features.adapters.colors.view

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.merseyside.adapters.core.async.addAsync
import com.merseyside.adapters.core.async.updateAsync
import com.merseyside.adapters.core.config.init.initAdapter
import com.merseyside.adapters.core.feature.filtering.Filtering
import com.merseyside.adapters.core.feature.filtering.ext.addFilterAsync
import com.merseyside.adapters.core.feature.filtering.ext.applyFiltersAsync
import com.merseyside.adapters.core.feature.filtering.ext.removeFilterAsync
import com.merseyside.adapters.core.feature.sorting.Sorting
import com.merseyside.adapters.core.modelList.update.UpdateBehaviour
import com.merseyside.adapters.delegates.composites.SimpleCompositeAdapter
import com.merseyside.adapters.delegates.feature.placeholder.Placeholder
import com.merseyside.adapters.delegates.feature.placeholder.textPlaceholder.TextPlaceholderProvider
import com.merseyside.adapters.delegates.feature.resolver.EmptyDataResolver
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.application.base.BaseSampleFragment
import com.merseyside.adapters.sample.databinding.FragmentColorsBinding
import com.merseyside.adapters.sample.features.adapters.colors.adapter.ColorsComparator
import com.merseyside.adapters.sample.features.adapters.colors.adapter.ColorsDelegateAdapter
import com.merseyside.adapters.sample.features.adapters.colors.adapter.ColorsFilter
import com.merseyside.adapters.sample.features.adapters.colors.di.ColorsModule
import com.merseyside.adapters.sample.features.adapters.colors.di.DaggerColorsComponent
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorsViewModel
import com.merseyside.archy.presentation.view.valueSwitcher.ValueSwitcher
import com.merseyside.merseyLib.kotlin.extensions.isZero
import com.merseyside.utils.ext.addTextChangeListener

class ColorsFragment : BaseSampleFragment<FragmentColorsBinding, ColorsViewModel>() {

    private val colorsFilter = ColorsFilter()
    private val colorsComparator = ColorsComparator(ColorsComparator.ColorComparisonRule.ASC)

    private val adapter = initAdapter(::SimpleCompositeAdapter) {
        coroutineScope = lifecycleScope

        Sorting {
            comparator = colorsComparator
        }

        Filtering {
            filter = colorsFilter
        }

        Placeholder {
            placeholderProvider = TextPlaceholderProvider("No colors. Press button below :)")
            placeholderDataResolver = EmptyDataResolver(addOnAttach = true)
        }
    }.apply { delegatesManager.addDelegates(ColorsDelegateAdapter()) }

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.fragment_colors
    override fun getTitle(context: Context) = "Colors"

    override fun performInjection(bundle: Bundle?, vararg params: Any) {
        DaggerColorsComponent.builder()
            .appComponent(appComponent)
            .colorsModule(ColorsModule(this))
            .build().inject(this)
    }

    private val textChangeListener = { view: View,
                                       newValue: String?,
                                       _: String?,
                                       length: Int,
                                       _: Int,
                                       _: Int,
                                       _: Int ->


        if (newValue != null) {
            val filterName = when (view.id) {
                requireBinding().rColor.id -> ColorsFilter.R_COLOR_FILTER
                requireBinding().gColor.id -> ColorsFilter.G_COLOR_FILTER
                requireBinding().bColor.id -> ColorsFilter.B_COLOR_FILTER
                else -> throw IllegalArgumentException()
            }

            if (length in 1..2) {
                colorsFilter.addFilterAsync(filterName, newValue)
                true
            } else {
                if (length.isZero()) {
                    colorsFilter.removeFilterAsync(filterName)
                    true
                } else {
                    false
                }
            }.also {
                colorsFilter.applyFiltersAsync()
            }
        } else false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireBinding().run {
            recyclerView.adapter = adapter
            rColor.addTextChangeListener(textChangeListener)
            gColor.addTextChangeListener(textChangeListener)
            bColor.addTextChangeListener(textChangeListener)
        }

        requireBinding().sortSwitcher.setOnValueChangeListener(
            object : ValueSwitcher.OnValueChangeListener {
                override fun valueChanged(entryValue: String) {
                    colorsComparator.setCompareRule(
                        ColorsComparator.ColorComparisonRule.valueOf(
                            entryValue.uppercase()
                        )
                    )
                }
            })


        viewModel.getColorsFlow().asLiveData().observe(viewLifecycleOwner) { colors ->
            if (requireBinding().add.isChecked) {
                adapter.addAsync(items = colors)
            } else {
                adapter.updateAsync(
                    colors,
                    UpdateBehaviour(
                        removeOld = requireBinding().updateRemove.isChecked,
                        addNew = requireBinding().updateAdd.isChecked
                    )
                )
            }
        }
    }

}