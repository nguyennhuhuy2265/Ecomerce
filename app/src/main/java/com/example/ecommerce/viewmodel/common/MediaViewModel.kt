package com.example.ecommerce.viewmodel.common

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.repository.CloudinaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MediaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CloudinaryRepository(application)
    private val _uploadResults = MutableLiveData<List<Result<String>>>()
    val uploadResults: LiveData<List<Result<String>>> get() = _uploadResults

    fun uploadImages(uris: List<Uri>, publicIdPrefix: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val results = uris.map { uri ->
                repository.uploadImage(uri, publicIdPrefix)
            }
            _uploadResults.postValue(results)
        }
    }
}