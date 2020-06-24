package com.victorsmind.launchonwakeup

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.*
import android.os.Build
import android.os.IBinder
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
        theFilter.addAction(STREAM_MUTE_CHANGED_ACTION)
        theFilter.addAction(INTENT)

        mPowerKeyReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val strAction = intent.action

                Log.e("onReceive", "onReceive called")
                if (strAction == Intent.ACTION_SCREEN_ON ||
                    strAction == Intent.ACTION_BOOT_COMPLETED
                ) {
                    Log.i("LaunchOnWakeUp", "Caught Intent!!!!! ${strAction}")

                    // > Your playground~!
                    val launchIntent: Intent? = packageManager.getLaunchIntentForPackage("tc.planeta.tv.stb")
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
        Log.i("LaunchOnWakeUp", "Broadcast receiver registered.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("LaunchOnWakeUp", "My service started")

        return super.onStartCommand(intent, flags, startId)
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

    companion object {
        val STREAM_MUTE_CHANGED_ACTION = "android.media.STREAM_MUTE_CHANGED_ACTION"
    }
}