package com.victorsmind.launchonwakeup

import android.app.Instrumentation
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private val storage = Storage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val intent = Intent(this, MainService::class.java)
        startService(intent)

        val dropdown1: Spinner = autoStartAppSpinner
        val dropdown2: Spinner = launcherAppSpinner
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val items = packages.map { it.packageName }.sorted()
        val adapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        val adapter2 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        dropdown1.adapter = adapter1
        dropdown2.adapter = adapter2

        val settings = storage.getSettings()

        settings.autoStartApp?.let {
            adapter1.getPosition(it)
        }?.let {
            dropdown1.setSelection(it)
        }
        settings.launcherApp?.let {
            adapter2.getPosition(it)
        }?.let {
            dropdown2.setSelection(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun sendMessage(view: View) {
        val launchIntent: Intent? = packageManager.getLaunchIntentForPackage("tc.planeta.tv.stb")
        if (launchIntent != null) {
            startActivity(launchIntent)
            thread {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
            }.start()

        }
    }

    fun onAutoStartAppButtonClick(view: View?) {
        runCatching {
            startIntent(storage.getSettings().autoStartApp)
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun onLauncherButtonClick(view: View?) {
        runCatching {
            startIntent(storage.getSettings().launcherApp)
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun onSaveButtonClick(view: View) {
        val autoStartApp = autoStartAppSpinner.selectedItem.toString()
        val launcherApp = launcherAppSpinner.selectedItem.toString()
        storage.getSettings().copy(autoStartApp = autoStartApp, launcherApp = launcherApp)
            .let { storage.setSettings(it) }
    }

    private fun startIntent(packageName: String?): Unit? =
        packageName
            ?.let {
                packageManager.getLaunchIntentForPackage(it)
                    ?: throw IllegalArgumentException("Package $it not found")
            }
            ?.let { startActivity(it) }
}
