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
import com.example.ecommerce.model.Review
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.repository.common.UserRepository
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class ReviewActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityReviewBinding
    private lateinit var viewModel: ImageUploadViewModel
    private val userRepo = UserRepository()
    private val reviewRepo = ReviewRepository()
    private lateinit var imageRepo: ImageUploadRepository
    private val scope = CoroutineScope(Dispatchers.Main)
    private var order: Order? = null
    private val imageAdapter = ImageAdapter() // Lưu URI tạm thời trước khi upload
    private var tempDocumentId: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                imageAdapter.addImage(uri.toString()) // Lưu URI tạm thời
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo imageRepo sau khi application đã sẵn sàng
        imageRepo = ImageUploadRepository(
            (application as com.example.ecommerce.config.MyApp).cloudinary,
            FirebaseFirestore.getInstance()
        )
        viewModel = ViewModelProvider(this, ImageUploadViewModel.Factory(imageRepo)).get(ImageUploadViewModel::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Lấy dữ liệu order
        val orderId = intent.getStringExtra("orderId")
        order = orderId?.let { id ->
            runBlocking {
                OrderRepository().getOrderById(id).getOrNull()
            }
        }

        // Hiển thị thông tin sản phẩm
        order?.let {
            Glide.with(this).load(it.productImage).into(binding.imgProduct)
            binding.tvProductName.text = it.productName
            binding.tvProductOptions.text = if (it.selectedOptions.isNotEmpty()) "Phân loại: ${it.selectedOptions.joinToString()}" else "Không có thông tin phân loại"
            binding.tvProductPrice.text = "đ${it.unitPrice}"
        }

        // Cấu hình RecyclerView
        binding.rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvImages.adapter = imageAdapter

        // Xử lý thêm ảnh
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

        // Xử lý upload ảnh (không cần observe trực tiếp, xử lý khi submit)
        viewModel.uploadStatus.observe(this) { status ->
            when (status) {
                is UploadStatus.Success -> {
                    // Không cần cập nhật ngay, xử lý khi submit
                }
                is UploadStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is UploadStatus.Loading -> {
                    // Có thể thêm UI loading nếu cần
                }
            }
        }

        // Xử lý nút back
        binding.btnBack.setOnClickListener { onBackPressed() }

        // Xử lý gửi đánh giá
        binding.btnSubmitReview.setOnClickListener {
            scope.launch {
                val user = userRepo.getCurrentUserInfo().getOrNull()
                val rating = binding.ratingBar.rating.toInt()
                val comment = binding.etComment.text.toString()
                val imageUris = imageAdapter.getImageUris() // Lấy danh sách URI tạm thời

                if (rating == 0) {
                    Toast.makeText(this@ReviewActivity, "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Tạo document ID khi submit
                val reviewId = FirebaseFirestore.getInstance().collection("reviews").document().id
                val uploadedImageUrls = mutableListOf<String>()

                // Upload ảnh và lấy URL
                imageUris.forEach { uri ->
                    val filePath = getRealPathFromURI(Uri.parse(uri)) ?: uri
                    viewModel.uploadImage(filePath, "reviews", reviewId)
                    viewModel.uploadStatus.observeForever { status ->
                        if (status is UploadStatus.Success) {
                            uploadedImageUrls.addAll(status.imageUrls)
                        }
                    }
                }

                // Chờ upload hoàn tất (cách này không lý tưởng, cần cải tiến với LiveData hoặc Job)
                while (uploadedImageUrls.size < imageUris.size) {
                    // Chờ một chút (có thể gây deadlock, nên dùng Job hoặc Flow)
                    kotlinx.coroutines.delay(500)
                }

                val review = Review(
                    productId = order?.productId ?: "",
                    userId = user?.id ?: "",
                    userName = user?.name,
                    userAvatarUrl = user?.avatarUrl,
                    rating = rating,
                    comment = comment.takeIf { it.isNotBlank() },
                    imageUrls = uploadedImageUrls,
                    createdAt = Timestamp.now()
                )

                reviewRepo.getReviewsByProductId(order?.productId ?: "").onSuccess { reviews ->
                    if (reviews.none { it.userId == user?.id }) {
                        reviewRepo.addReview(review).onSuccess {
                            Toast.makeText(this@ReviewActivity, "Gửi đánh giá thành công", Toast.LENGTH_SHORT).show()
                            finish()
                        }.onFailure {
                            Toast.makeText(this@ReviewActivity, "Lỗi khi lưu đánh giá: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ReviewActivity, "Bạn đã đánh giá sản phẩm này rồi", Toast.LENGTH_SHORT).show()
                    }
                }.onFailure {
                    Toast.makeText(this@ReviewActivity, "Lỗi khi kiểm tra đánh giá: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
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