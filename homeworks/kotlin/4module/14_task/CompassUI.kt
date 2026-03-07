package com.example.myapplication252636

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompassUI(azimuth: Float) {
    // Анимация угла поворота
    val animatedAngle by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 500, easing = LinearEasing),
        label = "compass_rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Компас",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1f))

        // Контейнер для компаса и буквы N
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            // Рисуем компас
            CompassNeedle(angle = animatedAngle)

            // Буква N (поворачивается вместе со стрелкой, чтобы оставаться на севере)
            Text(
                text = "N",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .rotate(animatedAngle) // поворачиваем текст в ту же сторону, что и стрелка
                    .align(Alignment.TopCenter)
                    .offset(y = (-20).dp) // поднимаем чуть выше центра
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Азимут: ${azimuth.toInt()}°",
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun CompassNeedle(angle: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val radius = size.minDimension / 2
        val center = Offset(radius, radius)

        // Рисуем тёмный круг фона
        drawCircle(
            color = Color.DarkGray,
            radius = radius,
            center = center
        )

        val arrowLength = radius * 0.8f
        val arrowWidth = arrowLength * 0.2f

        // Путь для северной (красной) половины стрелки
        val northPath = Path().apply {
            moveTo(center.x, center.y - arrowLength) // острие севера
            lineTo(center.x - arrowWidth / 2, center.y)
            lineTo(center.x + arrowWidth / 2, center.y)
            close()
        }

        // Путь для южной (серой) половины стрелки
        val southPath = Path().apply {
            moveTo(center.x, center.y + arrowLength) // острие юга
            lineTo(center.x - arrowWidth / 2, center.y)
            lineTo(center.x + arrowWidth / 2, center.y)
            close()
        }

        // Поворачиваем весь контекст на угол -angle, чтобы север совпадал с красной стрелкой
        // (т.к. azimuth — это угол от севера до направления телефона, нам нужно повернуть стрелку на -azimuth)
        rotate(degrees = -angle, pivot = center) {
            drawPath(northPath, color = Color.Red)
            drawPath(southPath, color = Color.Gray)
        }

        // Рисуем центральный кружок
        drawCircle(
            color = Color.White,
            radius = arrowWidth / 2,
            center = center
        )
    }
}