package com.victorsmind.launchonwakeup

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private val storage = Storage(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val intent = Intent(this, MainService::class.java)
        startService(intent)

        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val items = packages.map { it.packageName }.sorted()
        val adapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        val adapter2 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        autoStartAppSpinner.adapter = adapter1
        launcherAppSpinner.adapter = adapter2

        val settings = storage.getSettings()

        settings.autoStartApp?.let {
            adapter1.getPosition(it)
        }?.let {
            autoStartAppSpinner.setSelection(it)
        }
        settings.launcherApp?.let {
            adapter2.getPosition(it)
        }?.let {
            launcherAppSpinner.setSelection(it)
        }
        timeDelay.setText(settings.timeDelay.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
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
        val timeDelay = timeDelay.text.toString().toIntOrNull()!!
        storage.getSettings()
            .copy(autoStartApp = autoStartApp, launcherApp = launcherApp, timeDelay = timeDelay)
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