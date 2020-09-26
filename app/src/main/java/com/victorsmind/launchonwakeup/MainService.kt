package com.victorsmind.launchonwakeup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.SystemClock.sleep
import android.util.Log
import androidx.core.app.NotificationCompat

class MainService : Service() {
    private val storage = Storage(this)

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        Log.i("LaunchOnWakeUp.MainService", "onCreate")
        super.onCreate()
        registerBroadcastReceiver()

        if (Build.VERSION.SDK_INT >= 26) {
            val CHANNEL_ID = "your_channel_id"
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notification Channel Title",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )

            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("")
                .setContentText("").build()

            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver()
    }

    private var mPowerKeyReceiver: BroadcastReceiver? = null

    private fun registerBroadcastReceiver() {
        val theFilter = IntentFilter()
        /** System Defined Broadcast  */
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_BOOT_COMPLETED)

        mPowerKeyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.i("LaunchOnWakeUp.BroadcastReceiver", "onReceive")
                val strAction = intent.action
                if (strAction == Intent.ACTION_SCREEN_ON || strAction == Intent.ACTION_BOOT_COMPLETED) {
                    val settings = storage.getSettings()
                    settings.autoStartApp?.let {
                        packageManager.getLaunchIntentForPackage(it)
                    }?.let {
                        isServiceStarted = true
                        sleep(settings.timeDelay.toLong())
                        startActivity(it)
                    } /*?: Toast.makeText(this, "Inent could not be started.", Toast.LENGTH_SHORT)
                        .show()*/
                }
            }
        }

        applicationContext.registerReceiver(mPowerKeyReceiver, theFilter)
    }

    private fun unregisterReceiver() {
        val apiLevel = Build.VERSION.SDK_INT

        if (apiLevel >= 7) {
            try {
                applicationContext.unregisterReceiver(mPowerKeyReceiver)
            } catch (e: IllegalArgumentException) {
                mPowerKeyReceiver = null
            }

        } else {
            applicationContext.unregisterReceiver(mPowerKeyReceiver)
            mPowerKeyReceiver = null
        }
    }
}