package com.example.ecommerce.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.ecommerce.config.CloudinaryConfig
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

    suspend fun uploadImage(uri: Uri, publicId: String): Result<String> = withContext(Dispatchers.IO) {
        if (!isNetworkAvailable()) {
            return@withContext Result.failure(Exception("No network connection"))
        }

        try {
            val filePath = uri.toRealPath(context) ?: return@withContext Result.failure(Exception("Unable to resolve file path"))
            val mediaManager = CloudinaryConfig.getMediaManager()

            val imageUrl = suspendCancellableCoroutine<String> { continuation ->
                mediaManager.upload(filePath)
                    .unsigned("android_upload") // Thay bằng upload preset của bạn
                    .option("public_id", publicId)
                    .callback(object : UploadCallback {
                        override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                            val url = resultData?.get("secure_url")?.toString() ?: ""
                            continuation.resume(url)
                        }

                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            continuation.resumeWithException(Exception(error?.description ?: "Upload failed"))
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

// Helper extension để chuyển Uri thành đường dẫn thực tế
fun Uri.toRealPath(context: Context): String? {
    val filePathColumn = arrayOf(android.provider.MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, filePathColumn, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val columnIndex = it.getColumnIndexOrThrow(filePathColumn[0])
            return it.getString(columnIndex)
        }
    }
    return null
}