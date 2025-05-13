package com.example.ecommerce.ui.seller

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.LatestOrderAdapter // Thay đổi import
import com.example.ecommerce.databinding.SellerFragmentDashboardBinding
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.viewmodel.seller.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth

class DashboardFragment : Fragment() {

    private var _binding: SellerFragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var orderAdapter: LatestOrderAdapter // Thay đổi kiểu
    private lateinit var sellerId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SellerFragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            if (isAdded) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            }
            requireActivity().finish()
            return
        }
        println("Seller ID: $sellerId")

        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        orderAdapter = LatestOrderAdapter { order ->
            val intent = Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("orderId", order.id)
            }
            startActivity(intent)
        }

        setupRecyclerView()
        setupListeners()
        setupObservers()
        viewModel.loadDashboardData(sellerId)
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewCart.adapter = orderAdapter
        println("RecyclerView setup with adapter: ${orderAdapter.itemCount} items")
    }

    private fun setupListeners() {
        binding.btnPendingOrders.setOnClickListener {
            navigateToOrderFragment(OrderStatus.PENDING)
        }
        binding.btnPreparingOrders.setOnClickListener {
            navigateToOrderFragment(OrderStatus.CONFIRMED)
        }
        binding.btnShippingOrders.setOnClickListener {
            navigateToOrderFragment(OrderStatus.SHIPPING)
        }
        binding.btnViewAllOrders.setOnClickListener {
            navigateToOrderFragment(null)
        }
    }

    private fun navigateToOrderFragment(status: OrderStatus?) {
        val fragment = OrderFragment().apply {
            arguments = Bundle().apply {
                putString("selectedStatus", status?.name)
            }
        }
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvShopName.text = it.shopName ?: "Tên Shop"
                binding.tvShopRating.text = "⭐ ${it.rating}"
                binding.tvReviewCount.text = " (${it.reviewCount} đánh giá)"
                Glide.with(this)
                    .load(it.avatarUrl)
                    .placeholder(R.drawable.ic_load)
                    .circleCrop()
                    .into(binding.ivShopAvatar)
            }
        }

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            println("Orders received in DashboardFragment: $orders")
            orderAdapter.submitList(orders.take(3)) // Chỉ hiển thị 3 đơn hàng mới nhất
            binding.tvPendingCount.text = orders.count { it.status == OrderStatus.PENDING }.toString()
            binding.tvPreparingCount.text = orders.count { it.status == OrderStatus.CONFIRMED }.toString()
            binding.tvShippingCount.text = orders.count { it.status == OrderStatus.SHIPPING }.toString()
        }

        viewModel.revenue.observe(viewLifecycleOwner) { revenue ->
            binding.tvRevenue.text = "₫${revenue.toInt()}"
            println("Revenue updated: $revenue")
        }

        viewModel.soldCount.observe(viewLifecycleOwner) { count ->
            binding.tvSold.text = count.toString()
            println("Sold count updated: $count")
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                if (isAdded) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}