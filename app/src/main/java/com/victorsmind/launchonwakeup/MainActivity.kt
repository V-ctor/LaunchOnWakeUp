package com.victorsmind.launchonwakeup

import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var packageNameForStart: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
//        registBroadcastReceiver()

        val intent = Intent(this, MainService::class.java)
        startService(intent)

/*        button.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/
        //get the spinner from the xml.
//        val dropdown: Spinner = findViewById(R.id.activity_main)
        val dropdown: Spinner = spinner1
        spinner1.onItemSelectedListener = this;
//create a list of items for the spinner.
//        val items = arrayOf("1", "2", "three")
        val pm = packageManager
//get a list of installed apps.
//get a list of installed apps.
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val items = packages.map { it.packageName }
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
//        val adapter = ArrayAdapter(this, R.layout.simple_spinner_dropdown_item, items)
        val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter
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

    override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("Not yet implemented")
        packageNameForStart = p0?.getItemAtPosition(p2)?.toString()
    }

    fun onStartButtonClick(view: View?) {
        runCatching {
            packageNameForStart
                ?.let {
                    packageManager.getLaunchIntentForPackage(it)
                        ?: throw IllegalArgumentException("Package $it not found")
                }
                ?.let { startActivity(it) }
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}
