package com.merseyside.adapters.sample.features.adapters.racers.adapter

import com.merseyside.adapters.SimpleAdapter
import com.merseyside.adapters.core.config.AdapterConfig
import com.merseyside.adapters.core.config.config
import com.merseyside.adapters.core.holder.ViewHolder
import com.merseyside.adapters.core.holder.viewBinding.asBindingHolder
import com.merseyside.adapters.core.model.AdapterParentViewModel
import com.merseyside.animators.template.SetTextFadeOutInAnimator
import com.merseyside.adapters.sample.BR
import com.merseyside.adapters.sample.R
import com.merseyside.adapters.sample.databinding.ItemCheckpointBinding
import com.merseyside.adapters.sample.features.adapters.racers.entity.Checkpoint
import com.merseyside.adapters.sample.features.adapters.racers.model.CheckpointItemViewModel
import com.merseyside.merseyLib.time.units.Millis
import com.merseyside.merseyLib.time.units.compareTo
import com.merseyside.utils.ext.setTextColorAttr

class RacersAdapter(config: AdapterConfig<Checkpoint, CheckpointItemViewModel>) :
    SimpleAdapter<Checkpoint, CheckpointItemViewModel>(config) {
    override fun getLayoutIdForPosition(position: Int) = R.layout.item_checkpoint
    override fun getBindingVariable() = BR.viewModel
    override fun createItemViewModel(item: Checkpoint) = CheckpointItemViewModel(item)

    override fun onPayloadable(
        holder: ViewHolder<Checkpoint, CheckpointItemViewModel>,
        payloads: List<AdapterParentViewModel.Payloadable>
    ) {
        payloads.forEach {
            when (it) {
                is CheckpointItemViewModel.CheckpointPayloads.ChangeGap -> {
                    SetTextFadeOutInAnimator(
                        view = (holder.asBindingHolder().binding as ItemCheckpointBinding).gapChange,
                        text = getGapChangeFormatted(it.gapChange),
                        duration = Millis(500),
                        onInvisibleState = { view -> view.setTextColorAttr(getGapChangeColor(it.gapChange)) }
                    ).start()
                }
                else -> {}
            }
        }
    }

    private fun getGapChangeFormatted(gapChange: Millis): String {
        return if (gapChange.isNotEmpty()) {
            if (gapChange > 0) {
                "+ ${gapChange.millis} ms"
            } else {
                "${gapChange.millis} ms"
            }
        } else {
            ""
        }
    }

    private fun getGapChangeColor(gapChange: Millis): Int {
        return if (gapChange < 0) {
            R.attr.positive_text_color
        } else {
            R.attr.negative_text_color
        }
    }

    companion object {
        operator fun invoke(configure: AdapterConfig<Checkpoint, CheckpointItemViewModel>.() -> Unit): RacersAdapter {
            return RacersAdapter(config(configure))
        }
    }
}