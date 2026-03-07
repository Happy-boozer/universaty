package com.example.myapplication252636

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CompassViewModel : ViewModel() {
    private val _azimuth = MutableStateFlow(0f)
    val azimuth: StateFlow<Float> = _azimuth.asStateFlow()

    private val _sensorAvailable = MutableStateFlow(true)
    val sensorAvailable: StateFlow<Boolean> = _sensorAvailable.asStateFlow()

    fun updateAzimuth(newAzimuth: Float) {
        _azimuth.value = newAzimuth
    }

    fun setSensorAvailability(available: Boolean) {
        _sensorAvailable.value = available
    }
}