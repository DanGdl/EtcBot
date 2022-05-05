package com.mdgd.zombot.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils.SimpleStringSplitter
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.mdgd.zombot.R
import com.mdgd.zombot.bg.accessibility.MyAccessibilityService
import com.mdgd.zombot.bg.captor.ScreenCaptureService


class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var screenshotRequestLauncher = registerForActivityResult(CaptureRequestHandler()) {
        if (it.first == RESULT_OK) {
            startService(ScreenCaptureService.getStartIntent(this, it.first, it.second))
        }
    }
    private var accessibilityRequestLauncher =
        registerForActivityResult(AccessibilityRequestHandler()) {
            if (it == RESULT_OK) {
                onClick(findViewById(R.id.accessibility))
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.start).setOnClickListener(this)
        findViewById<View>(R.id.stop).setOnClickListener(this)
        findViewById<View>(R.id.accessibility).setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.start) {
            screenshotRequestLauncher.launch(1)
        } else if (p0?.id == R.id.stop) {
            stopService(ScreenCaptureService.getStopIntent(this))
        } else if (p0?.id == R.id.accessibility) {
            if (isAccessibilityEnabled()) {
                startService(Intent(this, MyAccessibilityService::class.java))
            } else {
                accessibilityRequestLauncher.launch(1)
            }
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        var accessibilityEnabled = 0
        val service = packageName + "/" + MyAccessibilityService::class.java.canonicalName
        val contentResolver = contentResolver
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        if (accessibilityEnabled == 1) {
            val settingValue: String = Settings.Secure.getString(
                contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            val mStringColonSplitter = SimpleStringSplitter(':')
            mStringColonSplitter.setString(settingValue)
            while (mStringColonSplitter.hasNext()) {
                val accessibilityService = mStringColonSplitter.next()
                if (accessibilityService.equals(service, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }


    inner class AccessibilityRequestHandler : ActivityResultContract<Int, Int>() {

        override fun createIntent(context: Context, input: Int?): Intent {
            return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            return resultCode
        }
    }

    inner class CaptureRequestHandler : ActivityResultContract<Int, Pair<Int, Intent?>>() {

        override fun createIntent(context: Context, input: Int?): Intent {
            return (getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager)
                .createScreenCaptureIntent()
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int, Intent?> {
            return Pair(resultCode, intent)
        }
    }
}
