package com.example.myapplication8596

import androidx.compose.material3.Icon
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication8596.ui.theme.MyApplication8596Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode

class CurrencyViewModel : ViewModel() {

    private val _currencyFlow = MutableStateFlow(90.5) // начальное значение
    val currencyFlow: StateFlow<Double> = _currencyFlow.asStateFlow()

    init {
        startPeriodicUpdates()
    }

    private fun startPeriodicUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(5000) // каждые 5 секунд
                generateNewRate()
            }
        }
    }

    fun forceUpdate() {
        generateNewRate()
    }

    private fun generateNewRate() {
        val newRate = (90.5 + (Math.random() * 4 - 2)) // диапазон 88.5 … 92.5
            .roundToTwoDecimals()
        _currencyFlow.value = newRate
    }

    private fun Double.roundToTwoDecimals(): Double =
        BigDecimal(this).setScale(2, RoundingMode.HALF_UP).toDouble()
}

enum class Trend { UP, DOWN, STABLE }

@Composable
fun CurrencyScreen(viewModel: CurrencyViewModel = viewModel()) {

    val rate by viewModel.currencyFlow.collectAsState()


    var previousRate by remember { mutableStateOf(rate) }


    val trend = when {
        rate > previousRate -> Trend.UP
        rate < previousRate -> Trend.DOWN
        else -> Trend.STABLE
    }


    LaunchedEffect(rate) {
        previousRate = rate
    }


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "USD to RUB",
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "%.2f".format(rate),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(8.dp))


            Icon(
                imageVector = when (trend) {
                    Trend.UP -> Icons.Default.ArrowUpward
                    Trend.DOWN -> Icons.Default.ArrowDownward
                    Trend.STABLE -> Icons.Default.HorizontalRule
                },
                contentDescription = null,
                tint = when (trend) {
                    Trend.UP -> Color.Green
                    Trend.DOWN -> Color.Red
                    Trend.STABLE -> Color.Gray
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { viewModel.forceUpdate() }) {
            Text("Обновить сейчас")
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication8596Theme {

                   CurrencyScreen()
            }
        }
    }
}

