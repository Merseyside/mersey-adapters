package com.merseyside.adapters.sample.features.adapters.racers.adapter

import com.merseyside.adapters.core.feature.sorting.comparator.Comparator
import com.merseyside.adapters.sample.features.adapters.racers.entity.Checkpoint
import com.merseyside.adapters.sample.features.adapters.racers.model.CheckpointItemViewModel

object RacersComparator : Comparator<Checkpoint, CheckpointItemViewModel>() {
    override fun compare(
        model1: CheckpointItemViewModel,
        model2: CheckpointItemViewModel
    ): Int {
        return model1.item.gap.compareTo(model2.item.gap)
    }
}