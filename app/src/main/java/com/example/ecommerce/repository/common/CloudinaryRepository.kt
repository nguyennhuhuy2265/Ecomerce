package com.example.ecommerce.repository

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.Transformation
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CloudinaryRepository(private val context: Context) {
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    suspend fun uploadImage(filePath: String, publicId: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(Exception("No network connection"))
        }

        try {
            val imageUrl = suspendCancellableCoroutine<String> { continuation ->
                MediaManager.get().upload(filePath)
                    .unsigned("android_upload")
                    .option("public_id", publicId)
                    .callback(object : UploadCallback {
                        override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                            val url = resultData?.get("url").toString()
                            continuation.resume(url)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            continuation.resumeWithException(Exception(error?.description))
                        }

                        override fun onStart(requestId: String?) {}
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                    }).dispatch()
            }
            Result.success(imageUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}