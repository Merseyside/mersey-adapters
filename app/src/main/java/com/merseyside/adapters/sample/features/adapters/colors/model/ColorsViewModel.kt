package com.merseyside.adapters.sample.features.adapters.colors.model

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.viewModelScope
import com.merseyside.adapters.sample.features.adapters.colors.entity.HexColor
import com.merseyside.adapters.sample.features.adapters.colors.producer.ColorProducer
import com.merseyside.archy.presentation.model.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ColorsViewModel(
    application: Application,
    private val colorProducer: ColorProducer
) : AndroidViewModel(application) {

    val isAddingObservableField = ObservableBoolean(true)
    val isFilterObservableField = ObservableBoolean(false)

    fun getColorsFlow(): Flow<List<HexColor>> = colorProducer.getColorsSharedFlow()

    fun onPopulateClick() {
        viewModelScope.launch {
            colorProducer.generateRandomColors(15)
        }
    }
}