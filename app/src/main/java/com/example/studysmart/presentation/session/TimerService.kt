package com.example.studysmart.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import com.example.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmart.util.Constants.ACTION_SERVICE_START
import com.example.studysmart.util.Constants.ACTION_SERVICE_STOP
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var timer: Timer
    private val binder = StudyTimerBinder()

    var duration: Duration = Duration.ZERO
        private set

    var seconds = mutableStateOf("00")
        private set

    var minutes = mutableStateOf("00")
        private set

    var hours = mutableStateOf("00")
        private set

    var currentTimerState = mutableStateOf(TimerState.CANCELLED)
        private set

    var subjectId = mutableStateOf<Int?>(null)


    override fun onBind(intent: Intent?): IBinder? = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.action.let {
            when (it) {
                ACTION_SERVICE_START -> {
                    startForegroundService()
                    startTimer{ h, m, s ->
                        notificationBuilder.setContentText("$h:$m:$s")
                        notificationManager.notify(1, notificationBuilder.build())
                    }
                }

                ACTION_SERVICE_STOP -> {
                    pauseTimer()
                }

                ACTION_SERVICE_CANCEL -> {
                    pauseTimer()
                    cancelTimer()
                    stopForegroundService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService() {
        createNotificationChannel()
        startForeground(1, notificationBuilder.build())
    }
    private fun stopForegroundService() {
        notificationManager.cancel(1)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "timer_channel_id",
            "Timer Channel",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(channel)

    }


    private fun startTimer(
        onTick: (h: String, m: String, s: String) -> Unit,
    ) {
        currentTimerState.value = TimerState.RUNNING
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            duration = duration.plus(1.seconds)
            updateTimeUnits()
            onTick(hours.value, minutes.value, seconds.value)
        }
    }

    private fun updateTimeUnits() {
        duration.toComponents { h, m, s, _ ->
            this@TimerService.hours.value = h.toString().padStart(2, '0')
            this@TimerService.minutes.value = m.toString().padStart(2, '0')
            this@TimerService.seconds.value = s.toString().padStart(2, '0')
//            Log.d("Time", "$s")

        }
    }

    private fun pauseTimer() {
        if (this::timer.isInitialized) {
            timer.cancel()
            currentTimerState.value = TimerState.PAUSED
        }
    }

    private fun cancelTimer() {
        duration = Duration.ZERO
        updateTimeUnits()
        currentTimerState.value = TimerState.CANCELLED
    }

    inner class StudyTimerBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

}


enum class TimerState {
    CANCELLED,
    RUNNING,
    PAUSED,
}