package com.mdgd.zombot.bg.captor

import android.app.Activity
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.util.Pair
import com.mdgd.zombot.ZomBotApp
import com.mdgd.zombot.bg.NotificationUtils

class ScreenCaptureService : Service() {

    companion object {

        private const val RESULT_CODE = "RESULT_CODE"
        private const val DATA = "DATA"
        private const val ACTION = "ACTION"
        private const val START = "START"
        private const val STOP = "STOP"

        fun getStartIntent(context: Context?, resultCode: Int, data: Intent?): Intent {
            val intent = Intent(context, ScreenCaptureService::class.java)
            intent.putExtra(ACTION, START)
            intent.putExtra(RESULT_CODE, resultCode)
            intent.putExtra(DATA, data)
            return intent
        }

        fun getStopIntent(context: Context?): Intent {
            val intent = Intent(context, ScreenCaptureService::class.java)
            intent.putExtra(ACTION, STOP)
            return intent
        }

        private fun isStartCommand(intent: Intent): Boolean {
            return (intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                    && intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == START)
        }

        private fun isStopCommand(intent: Intent): Boolean {
            return intent.hasExtra(ACTION) && intent.getStringExtra(ACTION) == STOP
        }
    }


    private val captor = ZomBotApp.getInstance()?.getComponent()?.captor
    private val cachedPrefs = ZomBotApp.getInstance()?.getComponent()?.cachedPrefs

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        cachedPrefs?.putCaptorRunning(true)
    }

    override fun onDestroy() {
        cachedPrefs?.putCaptorRunning(false)
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when {
            isStartCommand(intent) -> {
                val notification: Pair<Int, Notification> = NotificationUtils.getNotification(this)
                startForeground(notification.first, notification.second)
                val resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED)
                val data = intent.getParcelableExtra<Intent>(DATA)
                captor?.startProjection(resultCode, data)
            }
            isStopCommand(intent) -> {
                captor?.stopProjection()
                stopSelf()
            }
            else -> {
                captor?.stopProjection()
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }
}
