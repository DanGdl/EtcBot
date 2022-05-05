package com.mdgd.zombot.models.logger

import android.content.Context
import android.util.Log
import com.mdgd.zombot.BuildConfig
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val PATTERN_DATE = "yyyy_MM_dd"
const val PATTERN_TIME = "HH:mm:ss:SS"

class LoggerImpl(appCtx: Context) : Logger {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val handler = CoroutineExceptionHandler { _, e -> e.printStackTrace() }

    private val path = "${appCtx.filesDir.absolutePath}${File.separator}logs"
    private val jobs = mutableListOf<Job>()

    override fun log(message: String) {
        Log.d(TAG_LOGGER, message)
        if (!BuildConfig.SAVE_LOG && !BuildConfig.DEBUG) {
            return
        }
        val job = arrayOf<Job?>(null)
        job[0] = serviceScope.launch(handler) {
            val fileNameSdf = SimpleDateFormat(PATTERN_DATE, Locale.getDefault())
            val date = Date()
            val filename = fileNameSdf.format(date)
            fileNameSdf.applyPattern(PATTERN_TIME)
            writeToFile("${fileNameSdf.format(date)}: $message\n", filename)
            job[0]?.let { jobs.remove(it) }
        }
        job[0]?.let { jobs.add(it) }
    }

    private fun writeToFile(report: String, date: String) {
        val file = File("$path${File.separator}${date}.log")
        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        }
        var trace: FileOutputStream? = null
        try {
            trace = FileOutputStream(file, true)
            trace.write(report.encodeToByteArray()) // commonAsUtf8ToByteArray()
            trace.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            trace?.close()
        }
    }

    override fun log(error: LogException?) {
        if (error == null || (error.cause != null && error.cause is CancellationException)) {
            return
        }
        Log.d(TAG_LOGGER, "Exception ${error.message}, source: ${error.cause?.message}")
        error.printStackTrace()
        if (!BuildConfig.SAVE_LOG && !BuildConfig.DEBUG) {
            return
        }
        val job = arrayOf<Job?>(null)
        job[0] = serviceScope.launch(handler) {
            val date = Date()
            val fileNameSdf = SimpleDateFormat(PATTERN_DATE, Locale.getDefault())
            fileNameSdf.applyPattern(PATTERN_TIME)
            val report = "${fileNameSdf.format(date)}: ${getReport(error)}\n"
            fileNameSdf.applyPattern(PATTERN_DATE)
            writeToFile(report, fileNameSdf.format(date))
            job[0]?.let { jobs.remove(it) }
        }
        job[0]?.let { jobs.add(it) }
    }

    private fun getReport(error: Throwable?, initialReport: String = ""): String {
        if (error == null) {
            return initialReport
        }
        var report = if (initialReport.isEmpty()) {
            "$error\n"
        } else {
            "$initialReport\n"
        }

        for (element in error.stackTrace) {
            report += "\t$element\n"
        }
        return getReport(error.cause, report)
    }
}