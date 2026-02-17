package com.example.myapplication89

import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest
import kotlin.system.exitProcess

const val TIMEOUT_SECONDS = 10L // Общий таймаут в секундах

suspend fun main() = runBlocking {
    println("🔍 Программа поиска дубликатов JSON файлов")
    println("==========================================")

    // Получаем путь к директории
    val startPath = getStartPath()
    println("📁 Поиск в директории: $startPath")
    println("⏱ Таймаут: $TIMEOUT_SECONDS секунд")

    // Запускаем поиск с таймаутом
    val result = withTimeoutOrNull(TIMEOUT_SECONDS * 1000) {
        findJsonDuplicates(startPath)
    }

    // Обрабатываем результат
    when {
        result == null -> {
            println("\n⚠️ Поиск прерван по таймауту (${TIMEOUT_SECONDS} секунд)")
            println("Не все файлы были обработаны")
        }
        result.isEmpty() -> {
            println("\n✅ Дубликатов JSON файлов не найдено")
        }
        else -> {
            println("\n✅ Найдены дубликаты JSON файлов:")
            println("====================================")

            result.forEachIndexed { index, group ->
                println("\nГруппа ${index + 1} (${group.size} файлов):")
                group.forEach { file ->
                    println("  📄 ${file.absolutePath}")
                }
            }

            // Статистика
            val totalDuplicates = result.sumOf { it.size } - result.size
            println("\n📊 Статистика:")
            println("  Всего групп дубликатов: ${result.size}")
            println("  Всего файлов-дубликатов: $totalDuplicates")
        }
    }
}

// Функция для получения пути к директории
fun getStartPath(): String {

           return "C:\\Users\\Antipova\\Documents" // Текущая директория

}

// Функция для поиска дубликатов JSON файлов
suspend fun findJsonDuplicates(rootPath: String): List<List<File>> = coroutineScope {
    val rootDir = File(rootPath)

    if (!rootDir.exists() || !rootDir.isDirectory) {
        println("❌ Ошибка: Директория '$rootPath' не существует или не является директорией")
        return@coroutineScope emptyList()
    }

    println("🔎 Поиск JSON файлов...")

    // Находим все JSON файлы
    val jsonFiles = findJsonFiles(rootDir)

    if (jsonFiles.isEmpty()) {
        println("⚠️ JSON файлы не найдены")
        return@coroutineScope emptyList()
    }

    println("📊 Найдено JSON файлов: ${jsonFiles.size}")
    println("⚙️ Вычисление SHA-256 хешей...")

    // Создаем map для хранения хешей
    val hashToFilesMap = mutableMapOf<String, MutableList<File>>()

    // Запускаем параллельное вычисление хешей для всех файлов
    jsonFiles.map { file ->
        async {
            try {
                file to computeSha256(file)
            } catch (e: Exception) {
                println("⚠️ Ошибка при обработке файла '${file.name}': ${e.message}")
                file to null
            }
        }
    }.awaitAll().forEach { (file, hash) ->
        if (hash != null) {
            hashToFilesMap.getOrPut(hash) { mutableListOf() }.add(file)
        }
    }

    // Формируем группы дубликатов (оставляем только группы с более чем 1 файлом)
    return@coroutineScope hashToFilesMap.values
        .filter { it.size > 1 }
        .map { it.sortedBy { file -> file.path } } // Сортируем файлы в группе для удобства чтения
}

// Функция для рекурсивного поиска JSON файлов
fun findJsonFiles(directory: File): List<File> {
    val result = mutableListOf<File>()

    directory.listFiles()?.forEach { file ->
        when {
            file.isDirectory -> {
                result.addAll(findJsonFiles(file))
            }
            file.isFile && file.extension.equals("json", ignoreCase = true) -> {
                result.add(file)
            }
        }
    }

    return result
}

// Функция для вычисления SHA-256 хеша файла
suspend fun computeSha256(file: File): String = withContext(Dispatchers.IO) {
    try {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { inputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }

        // Преобразуем байты в hex строку
        digest.digest().joinToString("") { "%02x".format(it) }
    } catch (e: Exception) {
        throw Exception("Ошибка вычисления SHA-256 для файла '${file.name}': ${e.message}", e)
    }
}

// Функция для получения аргументов командной строки
fun getCommandLineArgs(): Array<String> {
    return try {
        // Получаем аргументы командной строки через систему
        // В реальном приложении здесь должен быть парсинг аргументов
        // Для простоты используем пустой массив
        emptyArray()
    } catch (e: Exception) {
        emptyArray()
    }
}
