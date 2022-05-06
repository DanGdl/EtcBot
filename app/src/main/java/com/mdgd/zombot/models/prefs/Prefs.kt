package com.mdgd.zombot.models.prefs

interface Prefs {
    fun getCaptorRunning(): Boolean
    fun putCaptorRunning(isRunning: Boolean)

    fun getAccessibilityRunning(): Boolean
    fun putAccessibilityRunning(isRunning: Boolean)

    fun getPackagesToHandle(): List<String>
    fun putPackagesToHandle(packages: List<String>)

    fun putBotActive(active: Boolean)
    fun getBotActive(): Boolean
}
