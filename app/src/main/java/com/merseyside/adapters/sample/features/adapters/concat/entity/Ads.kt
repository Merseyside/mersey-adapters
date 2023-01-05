package com.merseyside.adapters.sample.features.adapters.concat.entity

data class Ads private constructor(
    val id: Int,
    val title: String,
    val description: String
) {
    constructor(
        id: Int
    ): this(id, "Ads title $id", "Ads description $id")
}
