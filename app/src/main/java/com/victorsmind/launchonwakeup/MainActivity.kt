package com.victorsmind.launchonwakeup

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
        Log.i("MainActivity", "referrer='$referrer'")
        if (referrer?.authority == null || referrer?.authority == "android") {
            startThirdPartyLauncher() ?: createMainWindow()
        } else
            createMainWindow()
    }

    private fun createMainWindow() {
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val intent = Intent(this, MainService::class.java)
        startService(intent)

        val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val items = packages.map { it.packageName }.sorted()
        val adapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val installedLaunchers =
            packageManager.queryIntentActivities(launcherIntent, 0).map { it.activityInfo.packageName }.sorted()

        val adapter2 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, installedLaunchers)
        autoStartAppSpinner.adapter = adapter1
        launcherAppSpinner.adapter = adapter2

        val settings = storage.getSettings()

        settings.autoStartApp?.let {
            adapter1.getPosition(it)
        }?.let {
            autoStartAppSpinner.setSelection(it)
        } ?: autoStartAppSpinner.setSelection(0)

        settings.launcherApp?.let {
            adapter2.getPosition(it)
        }?.let {
            launcherAppSpinner.setSelection(it)
        } ?: launcherAppSpinner.setSelection(0)

        timeDelay.setText(settings.timeDelay.toString())
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAutoStartAppButtonClick(view: View?) {
        runCatching {
            startIntent(storage.getSettings().autoStartApp)
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onLauncherButtonClick(view: View?) = startThirdPartyLauncher()

    private fun startThirdPartyLauncher(): Intent? =
        runCatching {
            startIntent(storage.getSettings().launcherApp)
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }.getOrNull()

    @Suppress("UNUSED_PARAMETER")
    fun onSaveButtonClick(view: View) {
        val autoStartApp = autoStartAppSpinner.selectedItem.toString()
        val launcherApp = launcherAppSpinner.selectedItem.toString()
        val timeDelay = timeDelay.text.toString().toIntOrNull()!!
        storage.getSettings()
            .copy(autoStartApp = autoStartApp, launcherApp = launcherApp, timeDelay = timeDelay)
            .let { storage.setSettings(it) }
    }

    private fun startIntent(packageName: String?): Intent? =
        packageName
            ?.runCatching { packageManager.getLaunchIntentForPackage(this) }
            ?.onSuccess { startActivity(it) }
            ?.onFailure { it.printStackTrace() }
            ?.getOrNull()
}