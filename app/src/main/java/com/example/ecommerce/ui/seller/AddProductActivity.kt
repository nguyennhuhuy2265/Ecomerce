package com.example.ecommerce.ui.seller

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.OptionGroupAdapter
import com.example.ecommerce.config.CloudinaryConfig
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.OptionGroup
import com.example.ecommerce.model.OptionValue
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.ProductRepository
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.seller.adapter.ImageAdapter
import com.example.ecommerce.seller.viewmodel.CategoryViewModel
import com.example.ecommerce.viewmodel.common.MediaViewModel
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var etProductName: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var etPrice: EditText
    private lateinit var etStock: EditText
    private lateinit var rvImages: RecyclerView
    private lateinit var rvOptionGroups: RecyclerView
    private lateinit var btnAddImage: Button
    private lateinit var btnAddOptionGroup: Button
    private lateinit var btnSave: Button

    private val imageAdapter = ImageAdapter(mutableListOf())
    private val optionGroupAdapter = OptionGroupAdapter(mutableListOf()) { optionGroup ->
        // Xử lý khi chỉnh sửa OptionGroup (nếu cần)
    }
    private val mediaViewModel: MediaViewModel by viewModels()
    private val categoryViewModel: CategoryViewModel by viewModels()

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uriList ->
        if (uriList.isNotEmpty()) {
            mediaViewModel.uploadImages(uriList, "product_${UUID.randomUUID()}_")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seller_add_product)

        // Khởi tạo Cloudinary
        CloudinaryConfig.init(this)

        etProductName = findViewById(R.id.et_product_name)
        etDescription = findViewById(R.id.et_description)
        spinnerCategory = findViewById(R.id.spinner_category)
        etPrice = findViewById(R.id.et_price)
        etStock = findViewById(R.id.et_stock)
        rvImages = findViewById(R.id.rv_images)
        rvOptionGroups = findViewById(R.id.rv_option_groups)
        btnAddImage = findViewById(R.id.btn_add_image)
        btnAddOptionGroup = findViewById(R.id.btn_add_option_group)
        btnSave = findViewById(R.id.btn_save)

        // Cấu hình RecyclerView cho ảnh
        rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = imageAdapter

        // Cấu hình RecyclerView cho OptionGroups
        rvOptionGroups.layoutManager = LinearLayoutManager(this)
        rvOptionGroups.adapter = optionGroupAdapter

        // Lấy danh sách category từ ViewModel
        lifecycleScope.launch {
            val result = CategoryRepository().fetchCategories()
            if (result.isSuccess) {
                val categories = result.getOrNull() ?: emptyList()
                val categoryNames = categories.map { it.name }
                val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, categoryNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
                categoryViewModel.setCategories(categories)
            } else {
                // Xử lý lỗi (có thể hiển thị Toast)
            }
        }

        // Quan sát kết quả upload ảnh
        mediaViewModel.uploadResults.observe(this) { results ->
            val imageUrls = results.mapNotNull { result ->
                if (result.isSuccess) result.getOrNull() else null
            }
            imageAdapter.addImages(imageUrls)
        }

        btnAddImage.setOnClickListener {
            imagePicker.launch("image/*")
        }

        btnAddOptionGroup.setOnClickListener {
            val optionGroup = OptionGroup(
                id = UUID.randomUUID().toString(),
                name = "New Option Group",
                values = listOf(OptionValue(id = UUID.randomUUID().toString(), displayName = "Default"))
            )
            optionGroupAdapter.addOptionGroup(optionGroup)
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                val categories = categoryViewModel.categories.value ?: emptyList()
                val selectedCategory = categories.getOrNull(spinnerCategory.selectedItemPosition)
                if (selectedCategory != null) {
                    val product = Product(
                        id = "", // ID sẽ được Firestore tạo tự động
                        name = etProductName.text.toString(),
                        description = etDescription.text.toString(),
                        categoryId = selectedCategory.id,
                        price = etPrice.text.toString().toIntOrNull() ?: 0,
                        stock = etStock.text.toString().toIntOrNull() ?: 0,
                        imageUrls = imageAdapter.getImages(),
                        optionGroups = optionGroupAdapter.getOptionGroups(),
                        createdAt = Timestamp.now()
                    )
                    val result = ProductRepository().addProduct(product)
                    if (result.isSuccess) {
                        finish()
                    } else {
                        // Xử lý lỗi (có thể hiển thị Toast)
                    }
                } else {
                    // Xử lý trường hợp không có category (hiển thị Toast hoặc thông báo)
                }
            }
        }
    }
}