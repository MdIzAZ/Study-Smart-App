package com.example.studysmart.presentation.session

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.example.studysmart.MainActivity

object ServiceHelper {

    fun clickPendingIntent(context: Context): PendingIntent {
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "study_smart://session".toUri(),
            context,
            MainActivity::class.java
        )
        return TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                100,
                PendingIntent.FLAG_IMMUTABLE
            )!!
        }
    }

    fun triggerForegroundService(context: Context, action: String) {
        Intent(context, TimerService::class.java).also {
            it.action = action
            context.startService(it)
        }
    }
}