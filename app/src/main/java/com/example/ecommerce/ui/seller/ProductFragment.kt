package com.example.ecommerce.ui.seller

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ecommerce.databinding.FragmentSellerProductBinding
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.repository.ProductRepository
import com.example.ecommerce.viewmodel.common.MediaViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductFragment : Fragment() {
    private var _binding: FragmentSellerProductBinding? = null
    private val binding get() = _binding!!
    private val mediaViewModel: MediaViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    private val pickProductImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val filePath = getPathFromUri(uri)
                filePath?.let {
                    val productId = UUID.randomUUID().toString()
                    val category = binding.etCategory.text.toString().ifEmpty { "default_category" }
                    mediaViewModel.uploadImage(it, "products/$category/$productId")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.btnUploadProductImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickProductImageLauncher.launch(intent)
        }
    }

    private fun setupObservers() {
        mediaViewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { imageUrl ->
                val userId = auth.currentUser?.uid ?: return@onSuccess
                val productId = imageUrl.substringAfterLast("/").substringBeforeLast(".")
                val category = binding.etCategory.text.toString().ifEmpty { "default_category" }
                val product = Product(
                    id = productId,
                    name = binding.etProductName.text.toString(),
                    description = binding.etDescription.text.toString(),
                    categoryId = category,
                    categoryName = category,
                    sellerId = userId,
                    sellerName = "", // Có thể lấy từ Firestore
                    price = binding.etPrice.text.toString().toIntOrNull() ?: 0,
                    stock = binding.etStock.text.toString().toIntOrNull() ?: 0,
                    image_public_ids = listOf("products/$category/$productId"),
                    default_image_public_id = "products/$category/$productId",
                    createdAt = com.google.firebase.Timestamp.now()
                )
                CoroutineScope(Dispatchers.Main).launch {
                    val repo = ProductRepository()
                    repo.addProduct(product).onSuccess {
                        Toast.makeText(context, "Product added successfully", Toast.LENGTH_SHORT).show()
                    }.onFailure { e ->
                        Toast.makeText(context, "Failed to add product: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.onFailure { e ->
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPathFromUri(uri: android.net.Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        requireContext().contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}