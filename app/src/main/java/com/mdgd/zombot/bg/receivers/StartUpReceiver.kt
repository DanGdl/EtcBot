package com.mdgd.zombot.bg.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mdgd.zombot.ZomBotApp

class StartUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action
            || "android.intent.action.QUICKBOOT_POWERON" == intent?.action
        ) {
            ZomBotApp.getInstance()?.getComponent()?.logger?.log("Boot completed")
        } else {
            ZomBotApp.getInstance()
                ?.getComponent()?.logger?.log("StartUpReceiver action ${intent?.action}")
        }
    }
}
