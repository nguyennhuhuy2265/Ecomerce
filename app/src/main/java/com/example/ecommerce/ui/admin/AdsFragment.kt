//package com.example.ecommerce.ui.admin
//
//import android.Manifest
//import android.app.AlertDialog
//import android.content.pm.PackageManager
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Toast
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.activityViewModels
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.example.ecommerce.adapter.admin.BannerAdapter
//import com.example.ecommerce.config.MyApp
//import com.example.ecommerce.databinding.AdminFragmentAdsBinding
//import com.example.ecommerce.databinding.FragmentAdsBinding
//import com.example.ecommerce.repository.BannerRepository
//import com.example.ecommerce.repository.OperationStatus
//import com.example.ecommerce.viewmodel.admin.BannerViewModel
//import com.example.ecommerce.repository.common.BannerRepository
//import com.example.ecommerce.repository.common.ImageUploadRepository
//import com.example.ecommerce.repository.common.UploadStatus
//import com.example.ecommerce.viewmodel.common.ImageUploadViewModel
//import com.google.firebase.firestore.FirebaseFirestore
//import java.util.*
//
//class AdsFragment : Fragment() {
//    private var _binding: AdminFragmentAdsBinding? = null
//    private val binding get() = _binding!!
//    private val app: MyApp by lazy { requireActivity().application as MyApp }
//    private val viewModel: BannerViewModel by activityViewModels {
//        val imageUploadRepo = ImageUploadRepository(app.cloudinary, FirebaseFirestore.getInstance())
//        val bannerRepo = BannerRepository(imageUploadRepo)
//        BannerViewModel.Factory(bannerRepo)
//    }
//    private val imageUploadViewModel: ImageUploadViewModel by activityViewModels {
//        ImageUploadViewModel.Factory(ImageUploadRepository(app.cloudinary, FirebaseFirestore.getInstance()))
//    }
//    private val imageUrls = mutableListOf<String>()
//    private lateinit var adapter: BannerAdapter
//
//    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
//        uri?.let { imageUri ->
//            imageUploadViewModel.uploadImage(requireContext(), imageUri, "banners", UUID.randomUUID().toString())
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = AdminFragmentAdsBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Khởi tạo adapter và RecyclerView
//        adapter = BannerAdapter(imageUrls) { position ->
//            showDeleteConfirmationDialog(position)
//        }
//        binding.recyclerViewBanners.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = this@AdsFragment.adapter
//        }
//
//        // Xử lý nút Thêm ảnh
//        binding.buttonAddBanner.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
//                != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(
//                    requireActivity(),
//                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//                    1
//                )
//            } else {
//                pickImage.launch("image/*")
//            }
//        }
//
//        // Quan sát trạng thái upload ảnh
//        imageUploadViewModel.uploadStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                is UploadStatus.Success -> {
//                    val bannerUrl = status.imageUrls.first()
//                    imageUrls.add(bannerUrl)
//                    adapter.notifyDataSetChanged()
//                    viewModel.addBanner(bannerUrl)
//                }
//                is UploadStatus.Error -> {
//                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
//                }
//                is UploadStatus.Loading -> {
//                    // Có thể thêm UI loading nếu cần
//                }
//            }
//        }
//
//        // Quan sát trạng thái thao tác banner
//        viewModel.operationStatus.observe(viewLifecycleOwner) { status ->
//            when (status) {
//                is OperationStatus.Success -> {
//                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
//                }
//                is OperationStatus.Error -> {
//                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    // Xử lý yêu cầu quyền
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            pickImage.launch("image/*")
//        } else {
//            Toast.makeText(context, "Quyền truy cập bị từ chối", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // Hiển thị dialog xác nhận xóa
//    private fun showDeleteConfirmationDialog(position: Int) {
//        AlertDialog.Builder(requireContext())
//            .setTitle("Xác nhận xóa")
//            .setMessage("Bạn có chắc muốn xóa banner này không?")
//            .setPositiveButton("Xóa") { _, _ ->
//                val bannerUrl = imageUrls[position]
//                imageUrls.removeAt(position)
//                adapter.notifyItemRemoved(position)
//                viewModel.deleteBanner(bannerUrl)
//            }
//            .setNegativeButton("Hủy") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}