package com.merseyside.adapters.sample.features.adapters.news.model

import com.merseyside.merseyLib.time.units.TimeUnit

data class News(
    val id: Int,
    val title: String,
    val description: String,
    val time: TimeUnit
)
