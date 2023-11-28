package com.merseyside.adapters.sample.features.adapters.colors.adapter

import android.graphics.Color
import com.merseyside.adapters.core.feature.sorting.comparator.SimpleComparator
import com.merseyside.adapters.core.feature.sorting.async.updateAsync
import com.merseyside.adapters.core.feature.sorting.comparator.compareByType
import com.merseyside.adapters.core.model.VM
import com.merseyside.adapters.sample.features.adapters.colors.model.ColorItemViewModel
import java.lang.IllegalArgumentException

class ColorsComparator(private var comparisonRule: ColorComparisonRule) : SimpleComparator() {
    override fun compare(model1: VM<Any>, model2: VM<Any>): Int {
        return compareByType<ColorItemViewModel>(model1, model2) { m1, m2 ->
            when (comparisonRule) {
                ColorComparisonRule.ASC -> m1.getColor().compareTo(m2.getColor())
                ColorComparisonRule.DESC -> m2.getColor().compareTo(m1.getColor())
                ColorComparisonRule.RAINBOW -> rainbowComparison(m1.getColor(), m2.getColor())
            }
        } ?: throw IllegalArgumentException()
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