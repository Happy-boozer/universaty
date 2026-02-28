package com.example.myapplication

//import com.example.timerservice.MainActivity
import kotlin.jvm.java



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
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class TimerService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null
    private var seconds = 0
    private var isRunning = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "Service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_STOP -> {
                stopTimer()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun startTimer() {
        if (isRunning) return
        isRunning = true
        seconds = 0
        startForeground(NOTIFICATION_ID, createNotification())

        timerRunnable = object : Runnable {
            override fun run() {
                seconds++
                updateNotification()
                sendBroadcastUpdate()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable!!)
        Log.d(TAG, "Timer started")
    }

    private fun stopTimer() {
        if (!isRunning) return
        isRunning = false
        handler.removeCallbacks(timerRunnable!!)
        stopForeground(true)
        Log.d(TAG, "Timer stopped")
    }

    private fun updateNotification() {
        val notification = createNotification()
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Счётчик времени")
            .setContentText("Прошло $seconds секунд")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }

    private fun sendBroadcastUpdate() {
        val broadcastIntent = Intent(BROADCAST_ACTION)
        broadcastIntent.putExtra(EXTRA_TIME, seconds)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Timer Channel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Канал для счётчика времени"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopTimer()
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
    }

    companion object {
        const val TAG = "TimerService"
        const val CHANNEL_ID = "timer_channel"
        const val NOTIFICATION_ID = 1
        const val ACTION_START = "START"
        const val ACTION_STOP = "STOP"
        const val BROADCAST_ACTION = "TIMER_UPDATE"
        const val EXTRA_TIME = "current_time"
    }
}
