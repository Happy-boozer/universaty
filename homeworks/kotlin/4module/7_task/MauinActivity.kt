package com.example.myapplication45457

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var boundService: NumberGeneratorService? = null

    private val isBoundState = mutableStateOf(false)
    private val numberFlowState = mutableStateOf<Flow<Int>?>(null)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as NumberGeneratorService.LocalBinder
            boundService = binder.getService()
            isBoundState.value = true
            numberFlowState.value = boundService?.numberFlow
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
            isBoundState.value = false
            numberFlowState.value = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RandomNumberScreen(
                    onBind = { bindMyService() },
                    onUnbind = { unbindMyService() },
                    isBound = isBoundState.value,
                    numberFlow = numberFlowState.value
                )
            }
        }
    }

    private fun bindMyService() {
        if (!isBoundState.value) {
            Intent(this, NumberGeneratorService::class.java).also { intent ->
                bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    private fun unbindMyService() {
        if (isBoundState.value) {
            unbindService(connection)
            boundService = null
            isBoundState.value = false
            numberFlowState.value = null
        }
    }

    override fun onStop() {
        super.onStop()
        unbindMyService()
    }
}

@Composable
fun RandomNumberScreen(
    onBind: () -> Unit,
    onUnbind: () -> Unit,
    isBound: Boolean,
    numberFlow: Flow<Int>?
) {
    var currentNumber by remember { mutableStateOf(0) }

    LaunchedEffect(numberFlow) {
        if (numberFlow != null) {
            numberFlow.collectLatest { number ->
                currentNumber = number
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Случайное число: $currentNumber",
            fontSize = 32.sp,
            modifier = Modifier.padding(16.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBind,
                enabled = !isBound
            ) {
                Text("Подключиться")
            }
            Button(
                onClick = onUnbind,
                enabled = isBound
            ) {
                Text("Отключиться")
            }
        }
    }
}
