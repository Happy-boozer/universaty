package com.example.myapplication45457

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlin.random.Random

class NumberGeneratorService : Service() {
    private val binder = LocalBinder()
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _numberFlow = MutableSharedFlow<Int>(replay = 1, extraBufferCapacity = 0)
    val numberFlow: SharedFlow<Int> = _numberFlow

    private var generateJob: Job? = null

    inner class LocalBinder : Binder() {
        fun getService(): NumberGeneratorService = this@NumberGeneratorService
    }

    override fun onCreate() {
        super.onCreate()
        startGenerating()
    }

    private fun startGenerating() {
        generateJob = serviceScope.launch {
            while (isActive) {
                val random = Random.nextInt(0, 101) // 0..100
                _numberFlow.emit(random)
                delay(1000L)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onDestroy() {
        super.onDestroy()
        generateJob?.cancel()
        serviceScope.cancel()
    }
}