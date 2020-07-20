package com.victorsmind.launchonwakeup

import android.app.Instrumentation
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.PreferenceManager
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
    private val settings: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(
            this
        )
    }
    private val editor by lazy { settings.edit() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val intent = Intent(this, MainService::class.java)
        startService(intent)

        val dropdown1: Spinner = spinner1
        val dropdown2: Spinner = spinner2
        spinner1.onItemSelectedListener = this
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                editor.putString(LAUNCHER_PACKAGE_KEY, p0?.getItemAtPosition(p2)?.toString())
                editor.apply()
            }
        }
        val pm = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        val items = packages.map { it.packageName }.sorted()
        val adapter1 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        val adapter2 = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, items)
        dropdown1.adapter = adapter1
        dropdown2.adapter = adapter2

        settings.getString(AUTO_START_PACKAGE_KEY, null)?.let {
            adapter1.getPosition(it)
        }?.let {
            dropdown1.setSelection(it)
        }
        settings.getString(LAUNCHER_PACKAGE_KEY, null)?.let {
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
        if (launchIntent != null) {
            startActivity(launchIntent)
            thread {
                val inst = Instrumentation()
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
                inst.sendKeyDownUpSync(KeyEvent.KEYCODE_1)
            }.start()

        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
//        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//        TODO("Not yet implemented")
        editor.putString(AUTO_START_PACKAGE_KEY, p0?.getItemAtPosition(p2)?.toString())
        editor.apply()
    }

    fun onStartButtonClick(view: View?) {
        runCatching {
            startIntent(settings.getString(AUTO_START_PACKAGE_KEY, null))
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun onStartButtonClick2(view: View?) {
        runCatching {
            startIntent(settings.getString(LAUNCHER_PACKAGE_KEY, null))
        }.onFailure {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startIntent(packageName: String?): Unit? =
        packageName
            ?.let {
                packageManager.getLaunchIntentForPackage(it)
                    ?: throw IllegalArgumentException("Package $it not found")
            }
            ?.let { startActivity(it) }

    companion object {
        const val AUTO_START_PACKAGE_KEY = "autoStartPackageKey"
        const val LAUNCHER_PACKAGE_KEY = "launcherPackageKey"
    }
}
