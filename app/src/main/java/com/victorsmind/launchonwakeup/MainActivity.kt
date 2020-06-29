package com.victorsmind.launchonwakeup

import android.app.Instrumentation
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        registBroadcastReceiver()

        val intent = Intent(this, MainService::class.java)
        startService(intent)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun sendMessage(view: View) {
        val launchIntent: Intent? = packageManager.getLaunchIntentForPackage("tc.planeta.tv.stb")
//        val launchIntent: Intent? = packageManager.getLaunchIntentForPackage("ru.planeta.tv.activities.mytv.MyTvActivity")
/*        val launchIntent = Intent(Intent.ACTION_MAIN)
        launchIntent.setClassName(
            "tc.planeta.tv.stb",
            "ru.planeta.tv.activities.mytv.MyTvActivity"
        )*/
//        val launchIntent: Intent? = Intent()
//        launchIntent.component = ComponentName("tc.planeta.tv.stb", "ru.planeta.tv.activities.ShowcaseActivity")
//        launchIntent?.component = ComponentName("tc.planeta.tv.stb", "ru.planeta.tv.activities.PlaybackActivity")

        if (launchIntent != null) {
            startActivity(launchIntent)
//            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BUTTON_1)
            thread {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
//            Thread.sleep(20)
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
            }.start()
            /*           val mInputConnection =
                           BaseInputConnection(findViewById("tc.planeta.tv.stb"), true)
                       val kd = KeyEvent(
                           KeyEvent.ACTION_DOWN,
                           KeyEvent.KEYCODE_1
                       )
                       val ku = KeyEvent(
                           KeyEvent.ACTION_UP,
                           KeyEvent.KEYCODE_1
                       )
                       mInputConnection.sendKeyEvent(kd)
                       mInputConnection.sendKeyEvent(ku)*/
        }
    }
}
