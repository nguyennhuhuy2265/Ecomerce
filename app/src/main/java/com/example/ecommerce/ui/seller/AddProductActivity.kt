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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.ProductImageAdapter
import com.example.ecommerce.databinding.SellerActivityProductFormBinding
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.OptionGroup
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
import com.example.ecommerce.viewmodel.seller.ProductViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: SellerActivityProductFormBinding
    private lateinit var productViewModel: ProductViewModel
    private lateinit var imageUploadViewModel: ImageUploadViewModel
    private val imageUrls = mutableListOf<String>()
    private var categories: List<Category> = emptyList()
    private var tempDocumentId: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data
            imageUri?.let { uri ->
                val filePath = getRealPathFromURI(uri) ?: uri.toString()
                if (tempDocumentId == null) {
                    tempDocumentId = FirebaseFirestore.getInstance().collection("products").document().id
                }
                imageUploadViewModel.uploadImage(filePath, "products", tempDocumentId!!)
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
        supportActionBar?.title = "Thêm sản phẩm"

        val firestore = FirebaseFirestore.getInstance()
        val cloudinary = (application as com.example.ecommerce.config.MyApp).cloudinary
        val imageUploadRepository = ImageUploadRepository(cloudinary, firestore)
        productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
        imageUploadViewModel = ViewModelProvider(this, ImageUploadViewModel.Factory(imageUploadRepository)).get(ImageUploadViewModel::class.java)

        imageAdapter = ProductImageAdapter(imageUrls) { position ->
            imageUrls.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
        }
        binding.rvProductImages.apply {
            layoutManager = LinearLayoutManager(this@AddProductActivity, LinearLayoutManager.HORIZONTAL, false)
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
        }

        productViewModel.addProductSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        productViewModel.addProductError.observe(this) { error ->
            Toast.makeText(this, "Thêm sản phẩm thất bại: $error", Toast.LENGTH_SHORT).show()
        }

        binding.btnSaveProduct.setOnClickListener {
            saveProduct()
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

    private fun saveProduct() {
        val name = binding.etProductName.text.toString().trim()
        val description = binding.etProductDescription.text.toString().trim()
        val categoryName = binding.spinnerCategory.text.toString()
        val priceText = binding.etProductPrice.text.toString().trim()
        val stockText = binding.etProductStock.text.toString().trim()
        val location = binding.etProductLocation.text.toString().trim()

        // Lấy dữ liệu option groups
        val optionGroups = mutableListOf<OptionGroup>()

        // Option Group 1
        val optionGroup1Name = binding.etOptionGroup1.text.toString().trim()
        if (optionGroup1Name.isNotEmpty()) {
            val values = listOfNotNull(
                binding.etOption1Value1.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption1Value2.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption1Value3.text.toString().trim().takeIf { it.isNotEmpty() }
            )
            if (values.isNotEmpty()) {
                optionGroups.add(
                    OptionGroup(
                        id = UUID.randomUUID().toString(),
                        name = optionGroup1Name,
                        values = values
                    )
                )
            }
        }

        // Option Group 2
        val optionGroup2Name = binding.etOptionGroup2.text.toString().trim()
        if (optionGroup2Name.isNotEmpty()) {
            val values = listOfNotNull(
                binding.etOption2Value1.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption2Value2.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption2Value3.text.toString().trim().takeIf { it.isNotEmpty() }
            )
            if (values.isNotEmpty()) {
                optionGroups.add(
                    OptionGroup(
                        id = UUID.randomUUID().toString(),
                        name = optionGroup2Name,
                        values = values
                    )
                )
            }
        }

        // Option Group 3
        val optionGroup3Name = binding.etOptionGroup3.text.toString().trim()
        if (optionGroup3Name.isNotEmpty()) {
            val values = listOfNotNull(
                binding.etOption3Value1.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption3Value2.text.toString().trim().takeIf { it.isNotEmpty() },
                binding.etOption3Value3.text.toString().trim().takeIf { it.isNotEmpty() }
            )
            if (values.isNotEmpty()) {
                optionGroups.add(
                    OptionGroup(
                        id = UUID.randomUUID().toString(),
                        name = optionGroup3Name,
                        values = values
                    )
                )
            }
        }

        // Kiểm tra dữ liệu bắt buộc
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

        val sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Không tìm thấy thông tin người bán", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = tempDocumentId ?: FirebaseFirestore.getInstance().collection("products").document().id
        val product = Product(
            id = productId,
            name = name,
            description = description,
            categoryId = categoryId,
            sellerId = sellerId,
            price = price,
            stock = stock,
            imageUrls = imageUrls,
            shopLocation = location,
            createdAt = Timestamp.now(),
            optionGroups = optionGroups // Thêm optionGroups vào product
        )

        productViewModel.addProduct(product)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}