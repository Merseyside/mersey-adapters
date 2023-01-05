package com.merseyside.adapters.sample.features.adapters.racers.model

import android.app.Application
import androidx.databinding.ObservableBoolean
import com.merseyside.adapters.sample.features.adapters.racers.emulator.RacingEmulator
import com.merseyside.adapters.sample.features.adapters.racers.entity.Checkpoint
import com.merseyside.archy.presentation.model.AndroidViewModel
import kotlinx.coroutines.flow.Flow

class RacingViewModel(
    application: Application,
    private val racingEmulator: RacingEmulator
) : AndroidViewModel(application) {

    val isStarted = ObservableBoolean(false)

    fun getCheckpointFlow(): Flow<Checkpoint> {
        return racingEmulator.getCheckpointFlow()
    }

    fun start() {
        isStarted.set(!isStarted.get())

        if (isStarted.get()) {
            racingEmulator.startRacing()
        } else {
            racingEmulator.pauseRacing()
        }
    }
}