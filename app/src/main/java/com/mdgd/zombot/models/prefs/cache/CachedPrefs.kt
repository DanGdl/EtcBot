package com.mdgd.zombot.models.prefs.cache

import kotlinx.coroutines.flow.Flow

interface CachedPrefs {

    fun getBotActiveFlow(): Flow<Boolean>
    fun getBotActive(): Boolean
    fun putBotActive(isActive: Boolean)

    fun getCaptorRunningFlow(): Flow<Boolean>
    fun getCaptorRunning(): Boolean
    fun putCaptorRunning(isRunning: Boolean)

    fun getAccessibilityRunningFlow(): Flow<Boolean>
    fun getAccessibilityRunning(): Boolean
    fun putAccessibilityRunning(isRunning: Boolean)

    fun getPackagesToHandleFlow(): Flow<List<String>>
    fun getPackagesToHandle(): List<String>
    fun putPackagesToHandle(packages: List<String>)

    fun getHandledAppActiveFlow(): Flow<Boolean>
    fun getHandledAppActive(): Boolean
    fun putHandledAppActive(isActive: Boolean)
}
