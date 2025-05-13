package com.example.ecommerce.ui.seller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.SellerFragmentShopBinding
import com.example.ecommerce.ui.common.LoginActivity
import com.example.ecommerce.viewmodel.seller.ShopViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ShopFragment : Fragment() {
    private var _binding: SellerFragmentShopBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ShopViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SellerFragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Quan sát thông tin người bán
        viewModel.sellerInfo.observe(viewLifecycleOwner) { seller ->
            // Hiển thị thông tin cơ bản
            binding.tvName.text = seller.name
            binding.tvEmail.text = seller.email

            // Hiển thị thông tin tài khoản
            binding.tvPhone.text = seller.phoneNumber ?: "Chưa cập nhật số điện thoại"
            binding.tvAddress.text = seller.address?.let { address ->
                listOfNotNull(
                    address.streetNumber,
                    address.streetName,
                    address.ward,
                    address.district,
                    address.city,
                    address.country
                ).joinToString(", ")
            } ?: "Chưa cập nhật địa chỉ"
            binding.tvCreatedAt.text = seller.createdAt?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.toDate())
            } ?: "Chưa cập nhật"

            // Hiển thị thông tin cửa hàng
            binding.tvShopName.text = seller.shopName ?: "Cửa hàng chưa đặt tên"
            binding.tvShopCategory.text = seller.shopCategory ?: "Chưa chọn ngành hàng"
            binding.tvRating.text = "${seller.rating} (${seller.reviewCount} đánh giá)"

            // Hiển thị mặc định cho verified và followersCount
//            binding.tvVerification.text = "Đã xác thực" // Mặc định true
//            binding.tvFollowers.text = "1000 người theo dõi" // Mặc định 1000

            // Tải ảnh đại diện
            Glide.with(this)
                .load(seller.avatarUrl?.ifEmpty { null } ?: R.drawable.ic_load)
                .placeholder(R.drawable.ic_load)
                .into(binding.ivAvatar)
        }

        // Quan sát lỗi
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        // Quan sát đăng xuất
        viewModel.logoutSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }

        // Xử lý nút chỉnh sửa thông tin
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(requireContext(), EditShopActivity::class.java)
            startActivity(intent)
        }

        // Xử lý nút đăng xuất
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(this.context, LoginActivity::class.java))
        }

        binding.btnRefresh.setOnClickListener {
            viewModel.fetchSellerInfo()
            Toast.makeText(requireContext(), "Đã làm mới thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}