package com.example.ecommerce.ui.seller

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.config.MyApp
import com.example.ecommerce.databinding.SellerActivityEditShopBinding
import com.example.ecommerce.model.Address
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.example.ecommerce.viewmodel.seller.EditShopViewModel
import com.google.firebase.firestore.FirebaseFirestore

class EditShopActivity : AppCompatActivity() {
    private lateinit var binding: SellerActivityEditShopBinding
    private lateinit var editShopViewModel: EditShopViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val userId = editShopViewModel.userInfo.value?.id
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
        binding = SellerActivityEditShopBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editShopViewModel = ViewModelProvider(this).get(EditShopViewModel::class.java)

        val cloudinary = (application as MyApp).cloudinary
        val firestore = FirebaseFirestore.getInstance()
        val imageUploadRepository = ImageUploadRepository(cloudinary, firestore)
        imageUploadViewModel = ViewModelProvider(
            this,
            ImageUploadViewModel.Factory(imageUploadRepository)
        ).get(ImageUploadViewModel::class.java)

        setupToolbar()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupObservers() {
        editShopViewModel.userInfo.observe(this) { user ->
            binding.etName.setText(user.name)
            binding.etEmail.setText(user.email)
            binding.etPhone.setText(user.phoneNumber ?: "")
            binding.etShopName.setText(user.shopName ?: "")
            user.shopCategory?.let { category ->
                binding.spinnerShopCategory.setText(category, false)
            }
            user.address?.let { address ->
                binding.etStreetNumber.setText(address.streetNumber ?: "")
                binding.etStreetName.setText(address.streetName ?: "")
                binding.etWard.setText(address.ward ?: "")
                binding.etDistrict.setText(address.district ?: "")
                binding.etCity.setText(address.city ?: "")
                binding.etCountry.setText(address.country ?: "")
            } ?: run {
                binding.etStreetNumber.setText("")
                binding.etStreetName.setText("")
                binding.etWard.setText("")
                binding.etDistrict.setText("")
                binding.etCity.setText("")
                binding.etCountry.setText("")
            }
            Glide.with(this)
                .load(user.avatarUrl?.ifEmpty { null } ?: R.drawable.ic_load)
                .placeholder(R.drawable.ic_load)
                .into(binding.ivSellerAvatar)
        }

        editShopViewModel.categories.observe(this) { categories ->
            val categoryNames = categories.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
            binding.spinnerShopCategory.setAdapter(adapter)
        }

        editShopViewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Lưu thay đổi thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        editShopViewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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
                            .into(binding.ivSellerAvatar)
                        editShopViewModel.updateAvatar(imageUrl)
                    }
                }
                is UploadStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupListeners() {
        binding.fabChangeAvatar.setOnClickListener {
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

        binding.btnSave.setOnClickListener {
            val name = binding.etName.text?.toString()?.trim() ?: ""
            val phoneNumber = binding.etPhone.text?.toString()?.trim() ?: ""
            val shopName = binding.etShopName.text?.toString()?.trim() ?: ""
            val shopCategory = binding.spinnerShopCategory.text?.toString()?.trim() ?: ""
            val address = Address(
                streetNumber = binding.etStreetNumber.text?.toString()?.trim() ?: "",
                streetName = binding.etStreetName.text?.toString()?.trim() ?: "",
                ward = binding.etWard.text?.toString()?.trim() ?: "",
                district = binding.etDistrict.text?.toString()?.trim() ?: "",
                city = binding.etCity.text?.toString()?.trim() ?: "",
                country = binding.etCountry.text?.toString()?.trim() ?: ""
            )

            if (name.isEmpty() || shopName.isEmpty() || shopCategory.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            editShopViewModel.saveChanges(name, phoneNumber, shopName, shopCategory, address)

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