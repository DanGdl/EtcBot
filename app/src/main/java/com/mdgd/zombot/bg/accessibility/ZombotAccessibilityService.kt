package com.mdgd.zombot.bg.accessibility

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.mdgd.zombot.BuildConfig
import com.mdgd.zombot.ZomBotApp

class ZombotAccessibilityService : AccessibilityService() {

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, ZombotAccessibilityService::class.java)
        }

        fun isAccessibilityEnabled(ctx: Context): Boolean {
            var accessibilityEnabled = 0
            val service =
                "${ctx.packageName}/${ZombotAccessibilityService::class.java.canonicalName}"
            try {
                accessibilityEnabled = Settings.Secure.getInt(
                    ctx.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED
                )
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            if (accessibilityEnabled == 1) {
                val settingValue: String = Settings.Secure.getString(
                    ctx.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
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
    }


    private val logger = ZomBotApp.getInstance()?.getComponent()?.logger
    private val cachedPrefs = ZomBotApp.getInstance()?.getComponent()?.cachedPrefs

    override fun onCreate() {
        super.onCreate()
        cachedPrefs?.putAccessibilityRunning(true)
    }

    override fun onDestroy() {
        cachedPrefs?.putAccessibilityRunning(false)
        super.onDestroy()
    }

    override fun onInterrupt() {
        logger?.log("Accessibility onInterrupt")
        cachedPrefs?.putAccessibilityRunning(false)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        logger?.log("Accessibility onServiceConnected")
    }

    override fun onUnbind(intent: Intent): Boolean {
        logger?.log("Accessibility onServiceConnected")
        return super.onUnbind(intent)
    }

    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
        logger?.log("Accessibility onGesture")
        return super.onGesture(gestureEvent)
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        logger?.log("Accessibility onKeyEvent ${KeyEvent.keyCodeToString(event.keyCode)}")
        return onKeyEvent2(event) || super.onKeyEvent(event)
    }

    private fun onKeyEvent2(event: KeyEvent): Boolean {
// example
//        final String keyCode = KeyEvent.keyCodeToString(event.getKeyCode());
//        if ("KEYCODE_BACK".equals(keyCode)) {
//            for (IDelegate d : delegates.values()) {
//                d.onBackPressed();
//            }
//        }
//        if (isLauncherEnabled && 0/*ACTION_DOWN*/ == event.getAction() &&
//                ("KEYCODE_HOME".equals(keyCode) /*htc*/
//                        || "KEYCODE_MENU".equals(keyCode) /*xiaomi*/
//                        || "KEYCODE_APP_SWITCH".equals(keyCode) /*samsung*/
//                )) {
//            service.execGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
//            return true;
//        }
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        logEvent(event, null)
        if (event.packageName == null
            || AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED != event.eventType
        ) {
            return
        }
        when {
            cachedPrefs?.getPackagesToHandle()?.contains(event.packageName.toString()) == true -> {
                cachedPrefs.putHandledAppActive(true)
            }
            event.source?.childCount == 0 -> return // ignore
            else -> cachedPrefs?.putHandledAppActive(false)
        }
    }


    // <!-- debug functions
    private fun logEvent(event: AccessibilityEvent?, classFilter: String?) {
        if (event == null || !BuildConfig.DEBUG) {
            return
        }
        Log.d("AccessLOGG", "\n* phg ${event.packageName} type ${event.eventType}")
        showChildInfo(event.source, Rect(), 0, classFilter)
    }

    private fun showChildInfo(
        sourceX: AccessibilityNodeInfo?, rS: Rect, lvl: Int, filter: String?
    ) {
        if (sourceX == null) {
            return
        }
        val sb: StringBuilder = StringBuilder()
        for (i in 0 until lvl) {
            sb.append(" ")
        }
        val padding = sb.toString()
        if (lvl == 0) {
            logNode(sourceX, filter, rS, padding)
        }
        val count = sourceX.childCount
        for (i in 0 until count) {
            val child = sourceX.getChild(i)
            logNode(child, filter, rS, padding)
            showChildInfo(child, rS, lvl + 1, filter)
        }
        println()
    }

    private fun logNode(
        child: AccessibilityNodeInfo?, filter: String?, rS: Rect, padding: String
    ) {
        if (child != null) {
            if (child.className == null) {
                Log.d("AccessLOGG", "child $child")
            } else {
                val cls = child.className.toString()
                if (TextUtils.isEmpty(filter) || cls.contains(filter!!)) {
                    child.getBoundsInScreen(rS)
                    Log.d(
                        "AccessLOGG",
                        padding + "cls: " + cls + ", posS " + rS.toShortString() + ", desc "
                                + child.contentDescription + ", txt " + child.text
                    )
                }
            }
        }
    }
    // debug functions -->
}
