package com.merseyside.adapters.sample.features.adapters.colors.entity

import com.merseyside.merseyLib.kotlin.contract.Identifiable

data class HexColor(
    val color: Int
): Identifiable<Int> {

    override val id = color

    fun getHex(): String  {
        return String.format("#%06X", 0xFFFFFF and color)
    }

    fun getRHexColor(): String {
        return getHex().substring(1..2)
    }

    fun getGHexColor(): String {
        return getHex().substring(3..4)
    }

    fun getBHexColor(): String {
        return getHex().substring(5..6)
    }

    override fun toString(): String {
        return getHex()
    }
}