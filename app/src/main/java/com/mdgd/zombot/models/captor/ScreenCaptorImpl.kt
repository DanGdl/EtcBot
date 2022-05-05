package com.mdgd.zombot.models.captor

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.view.Display
import android.view.OrientationEventListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ScreenCaptorImpl(private val appCtx: Context) {

    private val virtualDisplayFlags =
        DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY or DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC
    private val captorName = "zombot_captor"
    private var IMAGES_PRODUCED = 0

    private val density = Resources.getSystem().displayMetrics.densityDpi
    private val storeDir = appCtx.filesDir.absolutePath + "/screenshots/"
    private val display: Display by lazy {
        val displayManager = appCtx.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.getDisplay(Display.DEFAULT_DISPLAY)
    }
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var handler: Handler? = null
    private var looper: Looper? = null
    private var width = 0
    private var height = 0
    private var rotation = 0

    private val imageAvailableListener = object : ImageReader.OnImageAvailableListener {
        override fun onImageAvailable(reader: ImageReader) {
            var fos: FileOutputStream? = null
            var bitmap: Bitmap? = null
            try {
                bitmap = imageReader?.acquireLatestImage()?.let { image ->
                    val planes = image.planes
                    val buffer = planes[0].buffer
                    val pixelStride = planes[0].pixelStride
                    val rowStride = planes[0].rowStride
                    val rowPadding = rowStride - pixelStride * width

                    // create bitmap
                    val b = Bitmap.createBitmap(
                        width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888
                    )
                    b.copyPixelsFromBuffer(buffer)

                    val f = File("$storeDir/screen_${IMAGES_PRODUCED}.png")
                    if (!f.exists()) {
                        f.parentFile?.mkdirs()
                        f.createNewFile()
                    }
                    // write bitmap to a file
                    fos = FileOutputStream(f)
                    b.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    IMAGES_PRODUCED++
                    image.close()
                    return@let b
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                fos?.let {
                    try {
                        it.close()
                    } catch (ioe: IOException) {
                        ioe.printStackTrace()
                    }
                }
                bitmap?.recycle()
            }
        }
    }

    private val stopCommand = object : MediaProjection.Callback() {

        override fun onStop() {
            handler?.post {
                clearDisplayAndReader()
                orientationListener.disable()
                mediaProjection?.unregisterCallback(this)
            }
        }
    }

    private val orientationListener = object : OrientationEventListener(appCtx) {

        override fun onOrientationChanged(orientation: Int) {
            val rotation = display.rotation
            if (rotation != this@ScreenCaptorImpl.rotation) {
                this@ScreenCaptorImpl.rotation = rotation
                clearDisplayAndReader()
                createVirtualDisplay()
            }
        }
    }

    init {
        val storeDirectory = File(storeDir)
        if (!storeDirectory.exists()) {
            if (storeDirectory.mkdirs()) {
                throw RuntimeException("Can't create a directory for screenshots")
            }
        }
    }


    fun startProjection(resultCode: Int, data: Intent?) {
        if (mediaProjection == null) {
            val mpManager =
                appCtx.getSystemService(Service.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mpManager.getMediaProjection(resultCode, data!!)
            mediaProjection?.let {
                object : Thread() {
                    override fun run() {
                        Looper.prepare()
                        looper = Looper.myLooper()
                        handler = Handler(Looper.myLooper()!!)
                        Looper.loop()
                    }
                }.start()

                createVirtualDisplay()
                if (orientationListener.canDetectOrientation()) {
                    orientationListener.enable()
                }
                it.registerCallback(stopCommand, handler)
            }
        }
    }

    fun stopProjection() {
        handler?.post {
            clearDisplayAndReader()
            mediaProjection?.stop()
            mediaProjection = null
            looper?.quit()
        }
    }

    private fun clearDisplayAndReader() {
        virtualDisplay?.release()
        imageReader?.setOnImageAvailableListener(null, null)
        imageReader?.close()
        imageReader = null
    }

    @SuppressLint("WrongConstant")
    private fun createVirtualDisplay() {
        // get width and height
        width = Resources.getSystem().displayMetrics.widthPixels
        height = Resources.getSystem().displayMetrics.heightPixels

        // start capture reader
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            captorName, width, height, density, virtualDisplayFlags,
            imageReader?.surface, null, handler
        )
        imageReader?.setOnImageAvailableListener(imageAvailableListener, handler)
    }
}
