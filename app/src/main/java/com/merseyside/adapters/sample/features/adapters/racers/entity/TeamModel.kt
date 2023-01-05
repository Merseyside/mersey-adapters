package com.merseyside.adapters.sample.features.adapters.racers.entity

import kotlinx.serialization.Serializable

@Serializable
data class TeamModel(
    val team: Team,
    val racers: List<RacerModel>
)