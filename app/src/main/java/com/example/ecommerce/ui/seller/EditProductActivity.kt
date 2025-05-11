package com.example.ecommerce.ui.seller

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.ProductImageAdapter
import com.example.ecommerce.databinding.SellerActivityProductFormBinding
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.example.ecommerce.viewmodel.seller.ProductViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class EditProductActivity : AppCompatActivity() {
    private lateinit var binding: SellerActivityProductFormBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private lateinit var product: Product
    private val imageUrls = mutableListOf<String>()
    private var categories: List<Category> = emptyList()

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                val filePath = getRealPathFromURI(uri) ?: uri.toString()
                imageUploadViewModel.uploadImage(filePath, "products", product.id ?: "temp_doc_id")
            }
        }
    }

    private lateinit var imageAdapter: ProductImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SellerActivityProductFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chỉnh sửa sản phẩm"

        val firestore = FirebaseFirestore.getInstance()
        val cloudinary = (application as com.example.ecommerce.config.MyApp).cloudinary
        val imageUploadRepository = ImageUploadRepository(cloudinary, firestore)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        imageUploadViewModel = ViewModelProvider(this, ImageUploadViewModel.Factory(imageUploadRepository)).get(ImageUploadViewModel::class.java)

        product = intent.getParcelableExtra("product") ?: run {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        imageUrls.addAll(product.imageUrls)
        imageAdapter = ProductImageAdapter(imageUrls) { position ->
            imageUrls.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        binding.rvProductImages.apply {
            layoutManager = LinearLayoutManager(this@EditProductActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }

        binding.btnAddImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    1)
            } else {
                openGallery()
            }
        }

        imageUploadViewModel.uploadStatus.observe(this) { status ->
            when (status) {
                is UploadStatus.Success -> {
                    imageUrls.addAll(status.imageUrls)
                    imageAdapter.notifyDataSetChanged()
                }
                is UploadStatus.Error -> {
                    Toast.makeText(this, status.message, Toast.LENGTH_SHORT).show()
                }
                is UploadStatus.Loading -> {
                    // Có thể thêm UI loading nếu cần
                }
            }
        }

        productViewModel.categories.observe(this) { categoryList ->
            categories = categoryList
            val categoryNames = categoryList.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categoryNames)
            binding.spinnerCategory.setAdapter(adapter)
            val selectedCategory = categories.find { it.id == product.categoryId }
            binding.spinnerCategory.setText(selectedCategory?.name, false)
        }

        productViewModel.updateProductSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        productViewModel.updateProductError.observe(this) { error ->
            Toast.makeText(this, "Cập nhật sản phẩm thất bại: $error", Toast.LENGTH_SHORT).show()
        }

        binding.etProductName.setText(product.name)
        binding.etProductDescription.setText(product.description)
        binding.etProductPrice.setText(product.price.toString())
        binding.etProductStock.setText(product.stock.toString())
        binding.etProductLocation.setText(product.shopLocation)

        binding.btnSaveProduct.setOnClickListener {
            updateProduct()
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

    private fun updateProduct() {
        val name = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val categoryName = binding.spinnerCategory.text.toString()
        val priceText = binding.etProductPrice.text.toString().trim()
        val stockText = binding.etProductStock.text.toString().trim()
        val location = binding.etProductLocation.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || categoryName.isEmpty() ||
            priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val price = priceText.toIntOrNull() ?: run {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }
        val stock = stockText.toIntOrNull() ?: run {
            Toast.makeText(this, "Số lượng không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCategory = categories.find { it.name == categoryName }
        val categoryId = selectedCategory?.id ?: run {
            Toast.makeText(this, "Danh mục không hợp lệ", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedProduct = product.copy(
            name = name,
            description = description,
            categoryId = categoryId,
            price = price,
            stock = stock,
            imageUrls = imageUrls,
            shopLocation = location
        )

        productViewModel.updateProduct(updatedProduct)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}

