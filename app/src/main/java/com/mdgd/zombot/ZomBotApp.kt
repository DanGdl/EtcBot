package com.mdgd.zombot

import androidx.multidex.MultiDexApplication
import com.mdgd.zombot.models.AppComponent
import com.mdgd.zombot.models.AppComponentImpl

class ZomBotApp : MultiDexApplication() {

    companion object {
        private var instance: ZomBotApp? = null

        fun getInstance() = instance
    }

    private var component: AppComponent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        component = AppComponentImpl(this)
    }

    fun getComponent() = component
}
