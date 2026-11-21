package services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import targets.LivelyGreetingTarget
import ui.activities.ConfigurationActivity

/**
 * Foreground service whose only job is to keep a receiver for ACTION_USER_PRESENT alive,
 * so the greeting can change on every unlock. Its notification uses a minimum importance
 * channel, which can be hidden from the system notification settings.
 */
class UnlockService : Service() {

    private val unlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Received ${intent.action}, refreshing target")
            SmartspacerTargetProvider.notifyChange(context, LivelyGreetingTarget::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Unlock refresh", NotificationManager.IMPORTANCE_MIN).apply {
                description = "Keeps the greeting updating on unlock. Safe to hide."
                setShowBadge(false)
            }
        )

        val openConfig = PendingIntent.getActivity(
            this, 0, Intent(this, ConfigurationActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setContentTitle("Lively Greeting")
            .setContentText("Changing the greeting on unlock")
            .setContentIntent(openConfig)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        ContextCompat.registerReceiver(
            this,
            unlockReceiver,
            // USER_PRESENT is the real unlock, SCREEN_ON is a fallback for devices
            // that don't deliver it reliably
            IntentFilter().apply {
                addAction(Intent.ACTION_USER_PRESENT)
                addAction(Intent.ACTION_SCREEN_ON)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) = START_STICKY

    override fun onDestroy() {
        unregisterReceiver(unlockReceiver)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "LivelyGreeting"
        private const val CHANNEL_ID = "unlock_refresh"
        private const val NOTIFICATION_ID = 1

        /** Starts or stops the service. Starting can fail when the app is in the background. */
        fun update(context: Context, enabled: Boolean) {
            val intent = Intent(context, UnlockService::class.java)
            if (enabled) {
                try {
                    ContextCompat.startForegroundService(context, intent)
                } catch (e: Exception) {
                    Log.w(TAG, "Could not start unlock service from background", e)
                }
            } else {
                context.stopService(intent)
            }
        }
    }
}
