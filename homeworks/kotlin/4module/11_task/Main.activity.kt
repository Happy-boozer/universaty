package com.example.myapplication7777



import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 100
    }

    private lateinit var tvStatus: TextView
    private lateinit var tvNextReminder: TextView
    private lateinit var btnToggle: Button
    private lateinit var statusIndicator: View
    private lateinit var switchReminder: SwitchMaterial

    private val viewModel: ReminderViewModel by viewModels()
    private lateinit var reminderManager: ReminderManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initViewModel()
        setupListeners()
        checkNotificationPermission()
    }

    private fun initViews() {
        tvStatus = findViewById(R.id.tv_status)
        tvNextReminder = findViewById(R.id.tv_next_reminder)
        btnToggle = findViewById(R.id.btn_toggle)
        statusIndicator = findViewById(R.id.status_indicator)
        switchReminder = findViewById(R.id.switch_reminder)

        reminderManager = ReminderManager(this)
    }

    private fun initViewModel() {
        // Наблюдаем за состоянием напоминания
        viewModel.reminderState.observe(this) { isEnabled ->
            updateUI(isEnabled)
        }

        // Наблюдаем за временем следующего напоминания
        viewModel.nextReminderTime.observe(this) { time ->
            updateNextReminderText(time)
        }
    }

    private fun setupListeners() {
        btnToggle.setOnClickListener {
            if (viewModel.reminderState.value == true) {
                turnOffReminder()
            } else {
                turnOnReminder()
            }
        }

        switchReminder.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                turnOnReminder()
            } else {
                turnOffReminder()
            }
        }
    }

    private fun turnOnReminder() {
        // Проверяем разрешение на уведомления для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
                return
            }
        }

        // Устанавливаем напоминание
        val nextReminderTime = reminderManager.setDailyReminder()
        viewModel.setReminderState(true)
        viewModel.setNextReminderTime(nextReminderTime)

        Toast.makeText(this, "Напоминание включено", Toast.LENGTH_SHORT).show()
    }

    private fun turnOffReminder() {
        reminderManager.cancelReminder()
        viewModel.setReminderState(false)
        viewModel.setNextReminderTime(0)

        Toast.makeText(this, "Напоминание выключено", Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(isEnabled: Boolean) {
        if (isEnabled) {
            tvStatus.text = "Включено"
            tvStatus.setTextColor(getColor(R.color.green))
            statusIndicator.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.green)
            btnToggle.text = "Выключить напоминание"
            switchReminder.isChecked = true
        } else {
            tvStatus.text = "Выключено"
            tvStatus.setTextColor(getColor(R.color.gray))
            statusIndicator.backgroundTintList =
                ContextCompat.getColorStateList(this, R.color.gray)
            btnToggle.text = "Включить напоминание"
            switchReminder.isChecked = false
            tvNextReminder.text = "Напоминание не установлено"
        }
    }

    private fun updateNextReminderText(timeInMillis: Long) {
        if (timeInMillis > 0) {
            val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val formattedTime = sdf.format(Date(timeInMillis))

            // Определяем, сегодня или завтра
            val dayText = if (isToday(timeInMillis)) "сегодня" else "завтра"
            tvNextReminder.text = "Следующее напоминание: $dayText в 20:00"
        } else {
            tvNextReminder.text = "Напоминание не установлено"
        }
    }

    private fun isToday(timeInMillis: Long): Boolean {
        val reminderDate = Date(timeInMillis)
        val today = Date()

        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(reminderDate) == sdf.format(today)
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Показываем объяснение, почему нужно разрешение
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.POST_NOTIFICATIONS)) {
                    showPermissionRationale()
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
            NOTIFICATION_PERMISSION_CODE)
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Разрешение на уведомления")
            .setMessage("Для работы напоминаний необходимо разрешение на показ уведомлений")
            .setPositiveButton("OK") { _, _ -> requestNotificationPermission() }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            NOTIFICATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение получено, включаем напоминание
                    turnOnReminder()
                } else {
                    Toast.makeText(this,
                        "Для работы напоминаний необходимо разрешение на уведомления",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
