package com.merseyside.adapters.sample.features.adapters.racers.entity

import com.merseyside.merseyLib.kotlin.contract.Identifiable
import com.merseyside.merseyLib.time.units.Millis

data class Checkpoint(
    val racer: RacerModel,
    val team: Team,
    val time: Millis,
    val gap: Millis
): Identifiable<String> {
    override val id = racer.name
}
