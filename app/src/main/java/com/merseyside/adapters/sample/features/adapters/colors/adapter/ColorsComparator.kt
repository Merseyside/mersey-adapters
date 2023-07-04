package com.merseyside.adapters.sample.features.adapters.colors.adapter

import android.graphics.Color
import com.merseyside.adapters.core.feature.sorting.comparator.SimpleComparator
import com.merseyside.adapters.core.feature.sorting.async.updateAsync
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel

class ColorsComparator(
    private var comparisonRule: ColorComparisonRule
) : SimpleComparator() {
    override fun compare(model1: VM<Any>, model2: VM<Any>): Int {
        return if (model1 == model2) 0
        else if (model1 !is ColorItemViewModel) -1
        else if (model2 !is ColorItemViewModel) 1
        else {
            return when (comparisonRule) {
                ColorComparisonRule.ASC -> model1.getColor().compareTo(model2.getColor())
                ColorComparisonRule.DESC -> model2.getColor().compareTo(model1.getColor())
                ColorComparisonRule.RAINBOW -> rainbowComparison(
                    model1.getColor(),
                    model2.getColor()
                )
            }
        }
    }

    private fun rainbowComparison(color1: Int, color2: Int): Int {
        val hsv1 = FloatArray(3)
        val hsv2 = FloatArray(3)

        Color.colorToHSV(color1, hsv1)
        Color.colorToHSV(color2, hsv2)

        if ((hsv1[0].toInt()) == (hsv2[0].toInt())) {
            return hsv1[2].toInt().compareTo(hsv2[2].toInt())
        }

        return hsv1[0].toInt().compareTo(hsv2[0].toInt())
    }

    fun setCompareRule(rule: ColorComparisonRule) {
        this.comparisonRule = rule
        updateAsync()
    }

    enum class ColorComparisonRule {
        ASC, DESC, RAINBOW
    }
}