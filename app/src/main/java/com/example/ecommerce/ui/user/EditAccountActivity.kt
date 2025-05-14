package com.example.ecommerce.ui.user

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.config.MyApp
import com.example.ecommerce.databinding.UserActivityEditAccountBinding
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Card
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.example.ecommerce.viewmodel.user.AccountViewModel
import com.google.firebase.firestore.FirebaseFirestore

class EditAccountActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityEditAccountBinding
    private lateinit var viewModel: AccountViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel

    // ActivityResultLauncher để chọn ảnh
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val userId = viewModel.user.value?.id
                if (userId == null) {
                    Toast.makeText(this, "Không tìm thấy user ID", Toast.LENGTH_SHORT).show()
                    return@let
                }
                val filePath = getRealPathFromURI(uri) ?: uri.toString()
                imageUploadViewModel.uploadImage(
                    filePath = filePath,
                    collection = "users",
                    documentId = userId,
                    fieldName = "avatarUrl"
                )
            }
        }
    }

    // ActivityResultLauncher để yêu cầu quyền truy cập bộ nhớ
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)

        // Khởi tạo ImageUploadViewModel
        val cloudinary = (application as MyApp).cloudinary
        val firestore = FirebaseFirestore.getInstance()
        val imageUploadRepository = ImageUploadRepository(cloudinary, firestore)
        imageUploadViewModel = ViewModelProvider(
            this,
            ImageUploadViewModel.Factory(imageUploadRepository)
        ).get(ImageUploadViewModel::class.java)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                it.address?.let { address ->
                    binding.etPhone.setText(it.phoneNumber ?: "")
                    binding.etStreetNumber.setText(address.streetNumber ?: "")
                    binding.etStreetName.setText(address.streetName ?: "")
                    binding.etWard.setText(address.ward ?: "")
                    binding.etDistrict.setText(address.district ?: "")
                    binding.etCity.setText(address.city ?: "")
                }
                it.card?.let { card ->
                    binding.etBankName.setText(card.bankName)
                    binding.etCardNumber.setText(card.cardNumber)
                    binding.etCardHolderName.setText(card.cardHolderName)
                    binding.etExpiryDate.setText(card.expiryDate)
                    binding.etCVV.setText(card.cvv)
                }
                // Load avatar nếu có
                Glide.with(this)
                    .load(it.avatarUrl?.ifEmpty { null } ?: R.drawable.ic_load)
                    .placeholder(R.drawable.ic_load)
                    .into(binding.ivAvatar)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        imageUploadViewModel.uploadStatus.observe(this) { status ->
            when (status) {
                is UploadStatus.Loading -> {
                    Toast.makeText(this, "Đang tải ảnh...", Toast.LENGTH_SHORT).show()
                }
                is UploadStatus.Success -> {
                    val imageUrl = status.imageUrls.firstOrNull()
                    if (imageUrl != null) {
                        Glide.with(this)
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_load)
                            .into(binding.ivAvatar)
                        // Cập nhật avatarUrl trong user
                        viewModel.user.value?.let { user ->
                            val updatedUser = user.copy(avatarUrl = imageUrl)
                            viewModel.updateUser(updatedUser)
                        }
                    }
                }
                is UploadStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }

        // Xử lý chọn ảnh khi nhấn vào nút chỉnh sửa avatar
        binding.btnEditAvatar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                pickImageLauncher.launch(intent)
            }
        }

        binding.btnUpdate.setOnClickListener {
            val updatedUser = viewModel.user.value?.copy(
                name = binding.etName.text.toString(),
                address = Address(
                    phoneNumber = binding.etPhone.text.toString(),
                    streetNumber = binding.etStreetNumber.text.toString(),
                    streetName = binding.etStreetName.text.toString(),
                    ward = binding.etWard.text.toString(),
                    district = binding.etDistrict.text.toString(),
                    city = binding.etCity.text.toString()
                ),
                card = Card(
                    bankName = binding.etBankName.text.toString(),
                    cardNumber = binding.etCardNumber.text.toString(),
                    cardHolderName = binding.etCardHolderName.text.toString(),
                    expiryDate = binding.etExpiryDate.text.toString(),
                    cvv = binding.etCVV.text.toString()
                )
            )

            updatedUser?.let { user ->
                viewModel.updateUser(user)
                Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show()
                finish()
            } ?: run {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show()
            }
        }
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
}