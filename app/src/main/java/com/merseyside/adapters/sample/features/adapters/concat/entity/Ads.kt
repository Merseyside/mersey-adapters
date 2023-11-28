package com.merseyside.adapters.sample.features.adapters.concat.entity

import com.merseyside.merseyLib.kotlin.contract.Identifiable

data class Ads private constructor(
    override val id: Int,
    val title: String,
    val description: String
): Identifiable<Int> {
    constructor(
        id: Int
    ): this(id, "Ads title $id", "Ads description $id")
}
