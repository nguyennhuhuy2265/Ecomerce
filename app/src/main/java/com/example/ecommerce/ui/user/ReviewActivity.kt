package com.example.ecommerce.ui.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ImageAdapter
import com.example.ecommerce.databinding.UserActivityReviewBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.example.ecommerce.viewmodel.user.ReviewViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityReviewBinding
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private lateinit var reviewViewModel: ReviewViewModel
    private lateinit var imageRepo: ImageUploadRepository
    private val scope = CoroutineScope(Dispatchers.Main)
    private var order: Order? = null
    private val imageAdapter = ImageAdapter()
    private var tempDocumentId: String? = null
    private val uploadedImageUrls = mutableListOf<String>()
    private var uploadCount = 0

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                imageAdapter.addImage(uri.toString())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageRepo = ImageUploadRepository(
            (application as com.example.ecommerce.config.MyApp).cloudinary,
            FirebaseFirestore.getInstance()
        )
        imageUploadViewModel = ViewModelProvider(this, ImageUploadViewModel.Factory(imageRepo)).get(ImageUploadViewModel::class.java)
        reviewViewModel = ViewModelProvider(this).get(ReviewViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val orderId = intent.getStringExtra("orderId")
        order = orderId?.let { id ->
            runBlocking {
                OrderRepository().getOrderById(id).getOrNull()
            }
        }

        order?.let {
            Glide.with(this).load(it.productImage).into(binding.imgProduct)
            binding.tvProductName.text = it.productName
            binding.tvProductOptions.text = if (it.selectedOptions.isNotEmpty()) "Phân loại: ${it.selectedOptions.joinToString()}" else "Không có thông tin phân loại"
            binding.tvProductPrice.text = "đ${it.unitPrice}"
        } ?: run {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.adapter = imageAdapter

        binding.tvImagesLabel.setOnClickListener {
            if (imageAdapter.itemCount >= 5) {
                Toast.makeText(this, "Bạn chỉ có thể thêm tối đa 5 ảnh", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
            } else {
                openGallery()
            }
        }

        binding.btnBack.setOnClickListener { onBackPressed() }

        binding.btnSubmitReview.setOnClickListener {
            scope.launch {
                val rating = binding.ratingBar.rating.toInt()
                val comment = binding.etComment.text.toString()
                val imageUris = imageAdapter.getImageUris()

                uploadedImageUrls.clear()
                uploadCount = 0

                if (imageUris.isEmpty()) {
                    tempDocumentId = FirebaseFirestore.getInstance().collection("reviews").document().id
                    order?.let {
                        reviewViewModel.submitReview(tempDocumentId!!, it.productId, rating, comment, emptyList())
                    }
                } else {
                    tempDocumentId = FirebaseFirestore.getInstance().collection("reviews").document().id
                    imageUris.forEach { uri ->
                        val filePath = getRealPathFromURI(Uri.parse(uri)) ?: uri
                        imageUploadViewModel.uploadImage(filePath, "reviews", tempDocumentId!!)
                    }

                    imageUploadViewModel.uploadStatus.observe(this@ReviewActivity) { status ->
                        when (status) {
                            is UploadStatus.Success -> {
                                uploadedImageUrls.addAll(status.imageUrls)
                                uploadCount++
                                if (uploadCount == imageUris.size) {
                                    order?.let {
                                        reviewViewModel.submitReview(tempDocumentId!!, it.productId, rating, comment, uploadedImageUrls)
                                    }
                                }
                            }
                            is UploadStatus.Error -> {
                                Toast.makeText(this@ReviewActivity, status.message, Toast.LENGTH_SHORT).show()
                            }
                            is UploadStatus.Loading -> {}
                        }
                    }
                }
            }
        }

        reviewViewModel.submitResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Gửi đánh giá thành công", Toast.LENGTH_SHORT).show()
                finish()
            }.onFailure {
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        reviewViewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(this, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImage.launch(intent)
    }

    private fun getRealPathFromURI(uri: Uri): String? {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(android.provider.MediaStore.Images.Media.DATA)
            it.moveToFirst()
            return it.getString(columnIndex)
        }
        return null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}