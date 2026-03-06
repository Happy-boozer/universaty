package com.example.myapplication21326545

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ProcessingScreen()
                }
            }
        }
    }
}

@Composable
fun ProcessingScreen(
    viewModel: PhotoProcessingViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // Текущий шаг
        Text(
            text = state.stepText.ifEmpty { "Нажмите кнопку для начала" },
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Прогресс
        if (state.isProcessing) {
            LinearProgressIndicator(
                progress = { state.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("${state.progress}%")
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопка запуска
        Button(
            onClick = { viewModel.startProcessing() },
            enabled = !state.isProcessing
        ) {
            Text("Начать обработку и загрузку")
        }

        // Кнопка отмены
        if (state.isProcessing) {
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { viewModel.cancelProcessing() }
            ) {
                Text("Отменить")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Результат
        state.resultMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        }

        // Ошибка
        state.errorMessage?.let {
            Text(
                text = "Ошибка: $it",
                color = MaterialTheme.colorScheme.error,
                fontSize = 18.sp
            )
        }
    }
}
