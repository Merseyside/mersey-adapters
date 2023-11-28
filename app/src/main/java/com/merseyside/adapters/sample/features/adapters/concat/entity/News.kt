package com.merseyside.adapters.sample.features.adapters.concat.entity

import com.merseyside.merseyLib.kotlin.contract.Identifiable

data class News private constructor(
    override val id: Int,
    val title: String,
    val description: String
): Identifiable<Int> {
    constructor(
        id: Int
    ): this(id, "News title $id", "News description $id")
}
