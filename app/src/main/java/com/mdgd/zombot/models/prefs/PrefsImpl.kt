package com.mdgd.zombot.models.prefs

import android.content.Context
import com.goldtouch.ynet.prefs.BasicPrefsImpl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val KEY_CAPTOR_RUNNING = "captor"
const val KEY_ACCESSIBILITY_RUNNING = "article_font_scale_hint_shown"
const val KEY_PACKAGES = "newest_flash_id"


class PrefsImpl(appCtx: Context) : BasicPrefsImpl(appCtx), Prefs {

    override fun getDefaultPrefsFileName(): String = "prefs_zombot"

    override fun getCaptorRunning() = get(KEY_CAPTOR_RUNNING, false)

    override fun putCaptorRunning(isRunning: Boolean) {
        put(KEY_CAPTOR_RUNNING, isRunning)
    }

    override fun getAccessibilityRunning() = get(KEY_ACCESSIBILITY_RUNNING, false)

    override fun putAccessibilityRunning(isRunning: Boolean) {
        put(KEY_ACCESSIBILITY_RUNNING, isRunning)
    }

    override fun getPackagesToHandle(): List<String> {
        val json = get(KEY_PACKAGES, "[]")
        return try {
            Gson().fromJson(json, object : TypeToken<List<String>>() {}.type) ?: listOf()
        } catch (e: Throwable) {
            e.printStackTrace()
            listOf()
        }
    }

    override fun putPackagesToHandle(packages: List<String>) {
        put(KEY_PACKAGES, Gson().toJson(packages)) // com.global.ztmslg
    }
}
