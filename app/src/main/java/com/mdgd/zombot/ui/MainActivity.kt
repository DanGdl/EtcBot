package com.mdgd.zombot.ui

import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.mdgd.zombot.R
import com.mdgd.zombot.bg.accessibility.ZombotAccessibilityService
import com.mdgd.zombot.bg.captor.ScreenCaptureService
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var screenshotRequestLauncher = registerForActivityResult(CaptureRequestHandler()) {
        if (it.first == RESULT_OK) {
            startService(ScreenCaptureService.getStartIntent(this, it.first, it.second))
        }
    }
    private var accessibilityRequestLauncher =
        registerForActivityResult(AccessibilityRequestHandler()) {
            if (it == RESULT_OK) {
                onClick(findViewById(R.id.accessibility_start))
            }
        }

    init {
        if(OpenCVLoader.initDebug()){
            Log.d("LOGG", "OpenCv configured successfully")
        } else{
            Log.d("LOGG", "OpenCv doesn’t configured successfully")
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.captor_start).setOnClickListener(this)
        findViewById<View>(R.id.captor_stop).setOnClickListener(this)
        findViewById<View>(R.id.accessibility_start).setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        if (p0?.id == R.id.captor_start) {
            screenshotRequestLauncher.launch(1)
        } else if (p0?.id == R.id.captor_stop) {
            stopService(ScreenCaptureService.getStopIntent(this))
        } else if (p0?.id == R.id.accessibility_start) {
            if (ZombotAccessibilityService.isAccessibilityEnabled(this)) {
                startService(ZombotAccessibilityService.getIntent(this))
            } else {
                accessibilityRequestLauncher.launch(1)
            }
        }
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
