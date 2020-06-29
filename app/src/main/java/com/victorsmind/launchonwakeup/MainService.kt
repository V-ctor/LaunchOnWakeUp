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
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        registBroadcastReceiver()


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
//        super.onDestroy()
        unregisterReceiver()
    }

    //------
    val INTENT = "mtk.intent.notify"
    private var mPowerKeyReceiver: BroadcastReceiver? = null

    private fun registBroadcastReceiver() {
        val theFilter = IntentFilter()
        /** System Defined Broadcast  */
        theFilter.addAction(Intent.ACTION_SCREEN_ON)
        theFilter.addAction(Intent.ACTION_BOOT_COMPLETED)

        mPowerKeyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val strAction = intent.action

                if (strAction == Intent.ACTION_SCREEN_ON || strAction == Intent.ACTION_BOOT_COMPLETED) {
                    Log.i("LaunchOnWakeUp", "Caught Intent!!!!! ${strAction}")

                    val launchIntent: Intent? =
                        packageManager.getLaunchIntentForPackage("tc.planeta.tv.stb")
                    sleep(1000)
//                    val launchIntent = Intent()
//                    launchIntent.component = ComponentName("tc.planeta.tv.stb"                    )
//                    startActivity(intent)
                    if (launchIntent != null) {
                        startActivity(launchIntent)//null pointer check in case package name was not found
//                        launchIntent.action
                        /*val inst = Instrumentation()
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BUTTON_1);
                        Thread.sleep(20);
                        inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BUTTON_1);*/
                    }

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