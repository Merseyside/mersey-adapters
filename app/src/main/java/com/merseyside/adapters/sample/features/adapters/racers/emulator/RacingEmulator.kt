package com.merseyside.adapters.sample.features.adapters.racers.emulator

import com.merseyside.adapters.sample.features.adapters.racers.entity.Checkpoint
import com.merseyside.adapters.sample.features.adapters.racers.entity.RacerModel
import com.merseyside.adapters.sample.features.adapters.racers.entity.Statistics
import com.merseyside.adapters.sample.features.adapters.racers.entity.TeamModel
import com.merseyside.merseyLib.time.units.Millis
import com.merseyside.merseyLib.time.units.TimeUnit
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class RacingEmulator(
    private val teams: List<TeamModel>,
    private val coroutineScope: CoroutineScope
) {

    private val racers: List<RacerModel> = teams.flatMap { it.racers }
    private val racersEngine: List<RacerEngine>
    private val statistics: Statistics =  Statistics(racers, provideLapCheckPointsAverageTime())

    private val callback = object : RacerEngine.RacerCallback {
        override suspend fun onCheckpoint(racer: RacerModel, checkpointTime: TimeUnit) {
            statistics.setCheckpointTime(racer, checkpointTime)

            checkpointSharedFlow.emit(
                Checkpoint(
                    racer = racer,
                    team = teams.find {it.racers.contains(racer)}?.team ?: "No team",
                    time = Millis(),
                    gap = statistics.getGap(racer)
                )
            )
        }

        override fun getNextCheckpointTime(racer: RacerModel): TimeUnit {
            return statistics.getNextCheckpointAverageTime(racer)
        }
    }

    init {
        racersEngine = racers.map {
            RacerEngine(it, callback, coroutineScope)
        }
    }

    private val checkpointSharedFlow = MutableSharedFlow<Checkpoint>()
    fun getCheckpointFlow(): Flow<Checkpoint> = checkpointSharedFlow

    fun startRacing() {
        racersEngine.forEach { it.start() }
    }

    fun pauseRacing() {
        racersEngine.forEach { it.pause() }
    }

    companion object {
        fun provideLapCheckPointsAverageTime() = listOf(
            12.34F, 11.62F, 8.6F, 17.78F, 15.41F, 13.9F, 26.71F
        )
    }
}