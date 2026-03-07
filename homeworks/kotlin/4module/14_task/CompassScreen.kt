package com.example.myapplication252636

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.*

@Composable
fun CompassScreen(viewModel: CompassViewModel = viewModel()) {
    val azimuth by viewModel.azimuth.collectAsState()
    val sensorAvailable by viewModel.sensorAvailable.collectAsState()

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val rotationSensor = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) }


    LaunchedEffect(rotationSensor) {
        viewModel.setSensorAvailability(rotationSensor != null)
    }


    DisposableEffect(Unit) {
        if (rotationSensor == null) return@DisposableEffect onDispose {}

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, it.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    // azimuth в радианах, переводим в градусы и нормализуем [0, 360)
                    var azimuthDeg = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    azimuthDeg = (azimuthDeg + 360) % 360
                    viewModel.updateAzimuth(azimuthDeg)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }


    if (!sensorAvailable) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Устройство не поддерживает датчик ориентации",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    } else {
        CompassUI(azimuth)
    }
}