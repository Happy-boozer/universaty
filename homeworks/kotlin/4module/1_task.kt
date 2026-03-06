package com.example.myapplication89

import android.R
import kotlinx.coroutines.*
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.random.Random
import kotlin.system.measureTimeMillis



@Serializable
data class SalesStats(val stats: Map<String, Int>)

@Serializable
data class WeatherData(val city: String, val temperature: Int)

suspend fun main() = coroutineScope {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        println(" Ошибка в корутине: ${throwable.message}")
    }

    println(" Начинаем выполнение параллельных задач...")

    val totalTime = measureTimeMillis {
        try {
            // Запускаем все три задачи параллельно
            val usersDeferred = async(exceptionHandler) { loadUsersWithDelay() }
            val salesDeferred = async(exceptionHandler) { loadSalesWithDelay() }
            val weatherDeferred = async(exceptionHandler) { loadWeatherWithDelay() }

            // Ожидаем завершения всех задач
            val results = awaitAll(usersDeferred, salesDeferred, weatherDeferred)

            // Выводим результаты
            println("\n Все задачи успешно выполнены!")
            println("\n Результаты:")

            if (results[0] != null) {
                println("\n👥 Список пользователей:")
                (results[0] as List<String>).forEach { println("  - $it") }
            }

            if (results[1] != null) {
                println("\n Статистика продаж за день:")
                (results[1] as Map<String, Int>).forEach { (product, count) ->
                    println("  - $product: $count шт.")
                }
            }

            if (results[2] != null) {
                println("\n Погода в городах:")
                (results[2] as List<String>).forEach { println("  - $it") }
            }

        } catch (e: Exception) {
            println("\n Произошла ошибка при выполнении задач: ${e.message}")
            println(" Детали ошибки: ${e.cause?.message ?: "Нет дополнительной информации"}")
        }
    }

    println("\n Общее время выполнения: ${totalTime}мс")
}

// Функция для загрузки пользователей с задержкой
suspend fun loadUsersWithDelay(): List<String> {
    return withContext(Dispatchers.IO) {
        //simulateRandomFailure("Загрузка пользователей")
        delay(1800)

        try {
            val jsonString = File("users.json").readText()
            val json = Json { ignoreUnknownKeys = true }
            val users = json.decodeFromString<Array<User>>(jsonString)
            //json.decodeFromString<>()
            users.map { it.name }
        } catch (e: Exception) {
            throw Exception("Ошибка загрузки пользователей: ${e.message}", e)
        }
    }
}

// Функция для загрузки статистики продаж с задержкой
suspend fun loadSalesWithDelay(): Map<String, Int> {
    return withContext(Dispatchers.IO) {
        //simulateRandomFailure("Загрузка статистики продаж")
        delay(3200)


        try {
            val jsonString = File("sales.json").readText()
            val json = Json { ignoreUnknownKeys = true }
            val sales = json.decodeFromString<SalesStats>(jsonString)
            sales.stats
        } catch (e: Exception) {
            throw Exception("Ошибка загрузки статистики продаж: ${e.message}", e)
        }
    }
}

// Функция для загрузки погоды с задержкой
suspend fun loadWeatherWithDelay(): List<String> {
    return withContext(Dispatchers.IO) {
        //simulateRandomFailure("Загрузка погоды")
        delay(2500)

        try {
            val jsonString = File("weather.json").readText()
            val json = Json { ignoreUnknownKeys = true }
            val weatherList = json.decodeFromString<List<WeatherData>>(jsonString)
            weatherList.map { "${it.city}: ${it.temperature}°C" }
        } catch (e: Exception) {
            throw Exception("Ошибка загрузки погоды: ${e.message}", e)
        }
    }
}

 //Функция для симуляции случайных сбоев (30% вероятность)
//fun simulateRandomFailure(operationName: String) {
//    if (Random.nextInt(100) < 30) { // 30% вероятность ошибки
//        throw RuntimeException("Случайный сбой в операции: $operationName")
//    }
//}
