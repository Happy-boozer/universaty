package com.example.myapplication66

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var etSeconds: EditText
    private lateinit var btnStart: Button
    private val REQUEST_CODE_POST_NOTIFICATIONS = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etSeconds = findViewById(R.id.etSeconds)
        btnStart = findViewById(R.id.btnStart)

        btnStart.setOnClickListener {
            val input = etSeconds.text.toString()
            if (input.isEmpty()) {
                Toast.makeText(this, "Введите количество секунд", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val seconds = input.toIntOrNull()
            if (seconds == null || seconds <= 0) {
                Toast.makeText(this, "Введите положительное число", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем разрешение на уведомления для Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                    startTimerService(seconds)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_POST_NOTIFICATIONS
                    )
                }
            } else {
                startTimerService(seconds)
            }
        }
    }

    private fun startTimerService(seconds: Int) {
        Intent(this, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_SECONDS, seconds)
            startService(this)
        }
        Toast.makeText(this, "Таймер запущен на $seconds секунд", Toast.LENGTH_SHORT).show()
        finish() // Закрываем активность (опционально)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Повторяем запуск с последним введённым значением
                val seconds = etSeconds.text.toString().toIntOrNull()
                if (seconds != null && seconds > 0) {
                    startTimerService(seconds)
                }
            } else {
                Toast.makeText(this, "Разрешение на уведомления необходимо для работы таймера", Toast.LENGTH_LONG).show()
            }
        }
    }
}
