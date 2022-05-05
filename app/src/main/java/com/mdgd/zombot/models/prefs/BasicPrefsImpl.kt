package com.goldtouch.ynet.prefs

import android.content.Context
import android.content.SharedPreferences

// TODO: migrate to DataStore https://developer.android.com/topic/libraries/architecture/datastore

abstract class BasicPrefsImpl(protected val ctx: Context) {

    abstract fun getDefaultPrefsFileName(): String

    protected fun getPrefs() = getPrefs(getDefaultPrefsFileName())

    protected fun getEditor() = getEditor(getDefaultPrefsFileName())

    private fun getEditor(fileName: String?): SharedPreferences.Editor = getPrefs(fileName).edit()

    private fun getPrefs(fileName: String?): SharedPreferences {
        // needs min sdk 23
        // import androidx.security.crypto.EncryptedSharedPreferences
        // import androidx.security.crypto.MasterKeys
        // try {
        //     // https://proandroiddev.com/encrypted-preferences-in-android-af57a89af7c8
        //     val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        //     return EncryptedSharedPreferences.create(
        //         fileName!!, masterKeyAlias, ctx,
        //         EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        //         EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        //     )
        // } catch (e: Throwable) {
        //     e.printStackTrace()
        // }
        return ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    }


    operator fun get(fileName: String, key: String, defaultVal: String): String? {
        return getPrefs(fileName).getString(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: String): String? {
        return getPrefs().getString(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: String) {
        getEditor(fileName).putString(key, value).apply()
    }

    fun put(key: String, value: String) {
        getEditor().putString(key, value).apply()
    }

    operator fun get(fileName: String, key: String, defaultVal: Int): Int {
        return getPrefs(fileName).getInt(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: Int): Int {
        return getPrefs().getInt(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: Int) {
        getEditor(fileName).putInt(key, value).apply()
    }

    fun put(key: String, value: Int) {
        getEditor().putInt(key, value).apply()
    }

    operator fun get(fileName: String, key: String, defaultVal: Boolean): Boolean {
        return getPrefs(fileName).getBoolean(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: Boolean): Boolean {
        return getPrefs().getBoolean(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: Boolean) {
        getEditor(fileName).putBoolean(key, value).apply()
    }

    fun put(key: String, value: Boolean) {
        getEditor().putBoolean(key, value).apply()
    }

    operator fun get(fileName: String, key: String, defaultVal: Long): Long {
        return getPrefs(fileName).getLong(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: Long): Long {
        return getPrefs().getLong(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: Long) {
        getEditor(fileName).putLong(key, value).apply()
    }

    fun put(key: String, value: Long) {
        getEditor().putLong(key, value).apply()
    }

    operator fun get(fileName: String, key: String, defaultVal: Float): Float {
        return getPrefs(fileName).getFloat(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: Float): Float {
        return getPrefs().getFloat(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: Float) {
        getEditor(fileName).putFloat(key, value).apply()
    }

    fun put(key: String, value: Float) {
        getEditor().putFloat(key, value).apply()
    }

    operator fun get(fileName: String, key: String, defaultVal: Set<String?>): Set<String>? {
        return getPrefs(fileName).getStringSet(key, defaultVal)
    }

    operator fun get(key: String, defaultVal: Set<String?>): Set<String>? {
        return getPrefs().getStringSet(key, defaultVal)
    }

    fun put(fileName: String, key: String, value: Set<String?>) {
        getEditor(fileName).putStringSet(key, value).apply()
    }

    fun put(key: String, value: Set<String?>) {
        getEditor().putStringSet(key, value).apply()
    }

    fun putSync(key: String, value: Set<String?>) {
        getEditor().putStringSet(key, value).commit()
    }

    operator fun get(fileName: String): Map<String, *> {
        return getPrefs(fileName).all
    }

    fun get(): Map<String, *> {
        return getPrefs().all
    }

    fun clear() {
        getEditor().clear().apply()
    }

    fun clear(fileName: String) {
        getEditor(fileName).clear().apply()
    }
}
