package com.example.myapplication89

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication89.ui.theme.MyApplication89Theme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.IOException
import java.io.InputStream

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

                RepositorySearchScreen()

                    }
    }
}
@Serializable
data class Repo(
    val full_name: String,
    val description: String,
    val stargazers_count: Int,
    val language: String
)

@Composable
fun RepositorySearchScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Состояния экрана
    var repositories by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var searchResults by remember { mutableStateOf<List<Repo>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isInitialLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Job для управления поиском с debounce
    var searchJob by remember { mutableStateOf<Job?>(null) }

    // Загрузка данных из JSON файла при инициализации с использованием async
    LaunchedEffect(Unit) {
        isInitialLoading = true
        errorMessage = null

        try {
            // Используем async для асинхронной загрузки
            val deferredRepos = coroutineScope.async(Dispatchers.IO) {
                loadRepositoriesFromJson(context)
            }

            // Ожидаем результат с помощью await()
            val loadedRepos = deferredRepos.await()

            repositories = loadedRepos
            searchResults = loadedRepos

        } catch (e: IOException) {
            errorMessage = "Ошибка загрузки файла: ${e.message}"
        } catch (e: IllegalArgumentException) {
            errorMessage = "Ошибка парсинга JSON: ${e.message}"
        } catch (e: Exception) {
            errorMessage = "Неизвестная ошибка: ${e.message}"
        } finally {
            isInitialLoading = false
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Поле поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query

                // Отменяем предыдущий поиск при новом вводе
                searchJob?.cancel()

                // Запускаем новый поиск с debounce
                searchJob = coroutineScope.launch {
                    // Debounce 300ms
                    delay(300)

                    // Проверяем, не была ли корутина отменена
                    if (!isActive) return@launch

                    // Показываем индикатор загрузки
                    isLoading = true

                    try {
                        // Выполняем поиск в фоновом потоке с использованием withContext
                        val results = withContext(Dispatchers.Default) {
                            performSearch(repositories, query)
                        }

                        // Проверяем, не была ли корутина отменена
                        if (isActive) {
                            // Обновляем результаты в главном потоке
                            searchResults = results
                        }
                    } catch (e: CancellationException) {
                        // Поиск был отменен - игнорируем
                        println("Search cancelled for query: $query")
                    } finally {
                        if (isActive) {
                            isLoading = false
                        }
                    }
                }
            },
            label = { Text("Поиск репозиториев") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = !isInitialLoading && errorMessage == null,
            singleLine = true
        )

        // Отображение различных состояний
        when {
            isInitialLoading -> {
                LoadingState()
            }
            errorMessage != null -> {
                ErrorState(
                    message = errorMessage!!,
                    onRetry = {
                        // Перезапуск загрузки при ошибке
                        coroutineScope.launch {
                            isInitialLoading = true
                            errorMessage = null

                            try {
                                val loadedRepos = withContext(Dispatchers.IO) {
                                    loadRepositoriesFromJson(context)
                                }
                                repositories = loadedRepos
                                searchResults = loadedRepos
                            } catch (e: Exception) {
                                errorMessage = "Повторная попытка не удалась: ${e.message}"
                            } finally {
                                isInitialLoading = false
                            }
                        }
                    }
                )
            }
            repositories.isEmpty() -> {
                EmptyState()
            }
            else -> {
                // Индикатор загрузки при поиске
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Список результатов
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = searchResults,
                        key = { repo -> repo.full_name } // Уникальный ключ для каждого элемента
                    ) { repo ->
                        RepositoryItem(repo = repo)
                    }

                    // Показываем сообщение, если ничего не найдено
                    if (searchResults.isEmpty() && !isLoading && searchQuery.isNotBlank()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ничего не найдено по запросу \"$searchQuery\"",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Функция загрузки репозиториев из JSON файла
private suspend fun loadRepositoriesFromJson(context: android.content.Context): List<Repo> =
    withContext(Dispatchers.IO) {
        try {
            // Добавляем небольшую задержку для демонстрации работы async/await
            // В реальном приложении эту строку можно удалить
            delay(1000)

            // Открываем файл из assets
            val inputStream: InputStream = context.assets.open("repositories.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            // Настраиваем Json парсер с игнорированием неизвестных ключей
            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

            // Парсим JSON в список репозиториев
            json.decodeFromString<List<Repo>>(jsonString)
        } catch (e: IOException) {
            throw IOException("Не удалось прочитать файл repositories.json", e)
        } catch (e: Exception) {
            throw IllegalArgumentException("Ошибка парсинга JSON: ${e.message}", e)
        }
    }

// Функция поиска с использованием withContext
private suspend fun performSearch(repositories: List<Repo>, query: String): List<Repo> =
    withContext(Dispatchers.Default) {
        // Имитация сложного поиска (можно удалить в реальном приложении)
        delay(100)

        if (query.isBlank()) {
            return@withContext repositories
        }

        repositories.filter { repo ->
            repo.full_name.contains(query, ignoreCase = true) ||
                    repo.description.contains(query, ignoreCase = true) ||
                    repo.language.contains(query, ignoreCase = true)
        }
    }

// Компоненты состояний
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Загрузка репозиториев...")
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "❌ Ошибка",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Нет доступных репозиториев",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun RepositoryItem(repo: Repo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = repo.full_name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = repo.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⭐ ${repo.stargazers_count}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = repo.language,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

