package com.example.ecommerce.viewmodel.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.repository.CloudinaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CloudinaryRepository(application)
    private val _uploadResult = MutableLiveData<Result<String>>()
    val uploadResult: LiveData<Result<String>> get() = _uploadResult

    fun uploadImage(filePath: String, publicId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.uploadImage(filePath, publicId)
            _uploadResult.postValue(result)
        }
    }
}