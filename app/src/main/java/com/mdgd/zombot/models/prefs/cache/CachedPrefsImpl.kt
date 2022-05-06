package com.mdgd.zombot.models.prefs.cache

import com.mdgd.zombot.models.prefs.Prefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CachedPrefsImpl(private val prefs: Prefs) : CachedPrefs {

    private val botActiveFlow = MutableStateFlow(prefs.getBotActive())
    private val captorFlow = MutableStateFlow(prefs.getCaptorRunning())
    private val accessibilityFlow = MutableStateFlow(prefs.getAccessibilityRunning())
    private val packagesFlow = MutableStateFlow(prefs.getPackagesToHandle())
    private val appHandleFlow = MutableStateFlow(prefs.getCaptorRunning())

    override fun getBotActiveFlow(): Flow<Boolean> = botActiveFlow

    override fun getBotActive() = botActiveFlow.value

    override fun putBotActive(isActive: Boolean) {
        botActiveFlow.tryEmit(isActive)
        prefs.putBotActive(isActive)
    }


    override fun getCaptorRunningFlow(): Flow<Boolean> = captorFlow

    override fun getCaptorRunning() = captorFlow.value

    override fun putCaptorRunning(isRunning: Boolean) {
        captorFlow.tryEmit(isRunning)
        prefs.putCaptorRunning(isRunning)
    }

    override fun getAccessibilityRunningFlow(): Flow<Boolean> = accessibilityFlow

    override fun getAccessibilityRunning() = accessibilityFlow.value

    override fun putAccessibilityRunning(isRunning: Boolean) {
        accessibilityFlow.tryEmit(isRunning)
        prefs.putAccessibilityRunning(isRunning)
    }

    override fun getPackagesToHandleFlow(): Flow<List<String>> = packagesFlow

    override fun getPackagesToHandle() = packagesFlow.value

    override fun putPackagesToHandle(packages: List<String>) {
        packagesFlow.tryEmit(packages)
        prefs.putPackagesToHandle(packages)
    }

    override fun getHandledAppActiveFlow(): Flow<Boolean> = appHandleFlow

    override fun getHandledAppActive(): Boolean = appHandleFlow.value

    override fun putHandledAppActive(isActive: Boolean) {
        appHandleFlow.tryEmit(isActive)
    }
}
