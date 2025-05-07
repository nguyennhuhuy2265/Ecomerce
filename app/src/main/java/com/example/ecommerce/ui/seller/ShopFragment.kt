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
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.FragmentSellerShopBinding
import com.example.ecommerce.repository.UserRepository
import com.example.ecommerce.viewmodel.common.MediaViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ShopFragment : Fragment() {
    private var _binding: FragmentSellerShopBinding? = null
    private val binding get() = _binding!!
    private val mediaViewModel: MediaViewModel by viewModels()
    private val auth = FirebaseAuth.getInstance()

    private val pickBannerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val filePath = getPathFromUri(uri)
                filePath?.let {
                    val userId = auth.currentUser?.uid ?: return@let
                    mediaViewModel.uploadImage(it, "banners/seller_$userId")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        setupObservers()
        loadShopBanner()
    }

    private fun setupListeners() {
        binding.btnUploadBanner.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickBannerLauncher.launch(intent)
        }
    }

    private fun setupObservers() {
        mediaViewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { imageUrl ->
                val userId = auth.currentUser?.uid ?: return@onSuccess
                CoroutineScope(Dispatchers.Main).launch {
                    val repo = UserRepository()
                    repo.updateBanner(userId, "banners/seller_$userId").onSuccess {
                        Toast.makeText(context, "Banner updated successfully", Toast.LENGTH_SHORT).show()
                        loadShopBanner()
                    }.onFailure { e ->
                        Toast.makeText(context, "Failed to update banner: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }.onFailure { e ->
                Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadShopBanner() {
        val userId = auth.currentUser?.uid ?: return
        val bannerUrl = mediaViewModel.getImageUrl("banners/seller_$userId", 800, 200, "fill")
        Glide.with(this)
            .load(bannerUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(binding.ivShopBanner)
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