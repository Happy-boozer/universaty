package com.example.myapplication66



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat

class TimerService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val seconds = intent?.getIntExtra(EXTRA_SECONDS, 0) ?: 0
        if (seconds <= 0) {
            stopSelf()
            return START_NOT_STICKY
        }

        Log.d(TAG, "Timer started for $seconds seconds")

        // Запускаем таймер
        timerRunnable = Runnable {
            showCompletionNotification()
            stopSelf() // Останавливаем сервис после показа уведомления
        }
        handler.postDelayed(timerRunnable!!, seconds * 1000L)

        return START_NOT_STICKY // Сервис не перезапускается, если убит системой
    }

    private fun showCompletionNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Таймер завершён!")
            .setContentText("Время вышло")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для уведомлений таймера"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        handler.removeCallbacks(timerRunnable!!) // Отменяем таймер, если сервис убивают досрочно
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    companion object {
        const val TAG = "TimerService"
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
        const val EXTRA_SECONDS = "extra_seconds"
    }
}
