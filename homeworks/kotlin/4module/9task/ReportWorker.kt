package com.example.myapplication323656

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield

class ReportWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // Получаем все результаты от предыдущих worker'ов
        val allData = inputData.keyValueMap

        // Собираем данные о городах и температурах
        val citiesData = mutableListOf<WeatherData>()
        var i = 0

        while (allData.containsKey("$KEY_CITY_$i")) {
            val city = allData["$KEY_CITY_$i"] as String
            val temp = allData["$KEY_TEMPERATURE_$i"] as Double
            citiesData.add(WeatherData(city, temp))
            i++
        }

        // Имитация формирования отчёта
        delay(1000)

        // Вычисляем среднюю температуру
        val averageTemp = citiesData.map { it.temperature }.average()
        val formattedTemp = String.format("%.1f", averageTemp)

        // Формируем итоговое сообщение
        val citiesList = citiesData.joinToString(", ") { it.city }
        val resultMessage = "Отчёт готов! Средняя температура: $formattedTemp°C (Города: $citiesList)"

        val outputData = workDataOf(KEY_RESULT to resultMessage)

        return Result.success(outputData)
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            WeatherWorker.NOTIFICATION_ID,
            WeatherNotificationManager.createNotification(
                applicationContext,
                "Формируем отчёт...",
                "Обработка полученных данных"
            )
        )
    }

    companion object {
        const val KEY_RESULT = "result"
        const val KEY_CITY_ = "city_"
        const val KEY_TEMPERATURE_ = "temperature_"
    }
}