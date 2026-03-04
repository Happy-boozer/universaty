package com.example.myapplication323656



import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlin.random.Random

class WeatherWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val city = inputData.getString(KEY_CITY) ?: return Result.failure()

        // Имитация сетевого запроса
        delay(Random.nextLong(2000, 5000))

        // Генерируем случайную температуру от -5 до 25
        val temperature = Random.nextDouble(-5.0, 25.0)

        Log.d("WeatherWorker", "Загружена погода для $city: $temperature°C")

        val outputData = workDataOf(
            KEY_CITY to city,
            KEY_TEMPERATURE to temperature
        )

        return Result.success(outputData)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            NOTIFICATION_ID,
            WeatherNotificationManager.createNotification(
                applicationContext,
                "Загружаем погоду...",
                "Получаем данные для городов"
            )
        )
    }

    companion object {
        const val KEY_CITY = "city"
        const val KEY_TEMPERATURE = "temperature"
        const val NOTIFICATION_ID = 100
    }
}