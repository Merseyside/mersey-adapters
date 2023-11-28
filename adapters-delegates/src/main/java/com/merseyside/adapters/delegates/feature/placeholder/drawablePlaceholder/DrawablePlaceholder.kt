package com.merseyside.adapters.delegates.feature.placeholder.drawablePlaceholder

import androidx.annotation.DrawableRes
import com.merseyside.merseyLib.kotlin.contract.Identifiable

data class DrawablePlaceholder(@DrawableRes val drawable: Int): Identifiable<String> {
    override val id: String = "drawable_placeholder"
}