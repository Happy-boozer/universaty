package com.example.myapplication323656



import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val workManager = WorkManager.getInstance(application)

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState

    private val cities = listOf("Москва", "Лондон", "Нью-Йорк", "Токио")
    private var workChainId: UUID? = null

    init {
        _uiState.value = _uiState.value.copy(cities = cities)
    }

    fun startLoading() {
        viewModelScope.launch {
            _uiState.value = WeatherUiState(
                cities = cities,
                isLoading = true,
                status = "Загружаем погоду для ${cities.size} городов..."
            )

            // Показываем начальное уведомление
            val context = getApplication<Application>()
            WeatherNotificationManager.updateNotification(
                context,
                "Загрузка прогноза",
                "Загружаем погоду для ${cities.size} городов..."
            )

            // Создаём параллельные worker'ы для каждого города
            val weatherWorks = cities.mapIndexed { index, city ->
                OneTimeWorkRequestBuilder<WeatherWorker>()
                    .setInputData(workDataOf(WeatherWorker.KEY_CITY to city))
                    .addTag("weather_$index")
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .build()
            }

            // Создаём worker для формирования отчёта
            val reportWork = OneTimeWorkRequestBuilder<ReportWorker>()
                .addTag("report")
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            // Запускаем параллельно и затем последовательный отчёт
            val continuation = workManager
                .beginWith(weatherWorks)
                .then(reportWork)

            continuation.enqueue()



            // Наблюдаем за прогрессом
            observeWorkProgress(weatherWorks.map { it.id } + reportWork.id)
        }
    }

    private fun observeWorkProgress(workIds: List<UUID>) {
        val context = getApplication<Application>()

        viewModelScope.launch {
            workManager.getWorkInfosFlow(
                WorkQuery.Builder.fromIds(workIds).build()
            ).collect { workInfos ->

                val weatherWorks = workInfos.filter { !it.tags.contains("report") }
                val reportWork = workInfos.firstOrNull { it.tags.contains("report") }

                val completedCount = weatherWorks.count { it.state == WorkInfo.State.SUCCEEDED }
                val totalCount = weatherWorks.size
                val failedWork = workInfos.firstOrNull { it.state == WorkInfo.State.FAILED }

                val status = when {
                    failedWork != null -> {
                        val failedCity =
                            failedWork.outputData.getString(WeatherWorker.KEY_CITY)
                                ?: "неизвестный город"
                        "Ошибка загрузки для $failedCity"
                    }

                    reportWork?.state == WorkInfo.State.RUNNING -> {
                        "Все данные получены, формируем отчёт..."
                    }

                    reportWork?.state == WorkInfo.State.SUCCEEDED -> {
                        val result =
                            reportWork.outputData.getString(ReportWorker.KEY_RESULT)
                                ?: "Отчёт готов!"

                        WeatherNotificationManager.updateNotification(
                            context,
                            "Отчёт готов!",
                            result
                        )

                        result
                    }

                    completedCount < totalCount -> {
                        val completedCities = weatherWorks
                            .filter { it.state == WorkInfo.State.SUCCEEDED }
                            .mapNotNull { it.outputData.getString(WeatherWorker.KEY_CITY) }

                        val inProgress = totalCount - completedCount

                        val citiesText = if (completedCities.isNotEmpty()) {
                            "Готово: ${completedCities.joinToString(", ")}"
                        } else {
                            "Загружаем данные..."
                        }

                        "$citiesText, ещё $inProgress городов..."
                    }

                    else -> "Все данные получены, готовим отчёт..."
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = workInfos.any {
                        it.state == WorkInfo.State.RUNNING ||
                                it.state == WorkInfo.State.ENQUEUED
                    },
                    status = if (reportWork?.state == WorkInfo.State.SUCCEEDED) "" else status,
                    result = if (reportWork?.state == WorkInfo.State.SUCCEEDED)
                        reportWork.outputData.getString(ReportWorker.KEY_RESULT) ?: ""
                    else "",
                    error = failedWork?.outputData?.getString(WeatherWorker.KEY_CITY)
                        ?.let { "Ошибка загрузки для $it" } ?: "",
                    completedCities = completedCount,
                    totalCities = totalCount
                )
            }
        }
    }

    data class WeatherUiState(
        val cities: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val status: String = "",
        val result: String = "",
        val error: String = "",
        val completedCities: Int = 0,
        val totalCities: Int = 0
    )
}