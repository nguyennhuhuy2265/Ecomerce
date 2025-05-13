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
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.OrderAdapter
import com.example.ecommerce.databinding.SellerFragmentOderBinding
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.viewmodel.seller.OrderViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth

class OrderFragment : Fragment() {

    private var _binding: SellerFragmentOderBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: OrderViewModel
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var sellerId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SellerFragmentOderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(context, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            requireActivity().finish()
            return
        }

        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        orderAdapter = OrderAdapter { order ->
            val intent = Intent(context, OrderDetailActivity::class.java).apply {
                putExtra("orderId", order.id)
            }
            startActivity(intent)
        }

        setupRecyclerView()
        setupTabLayout()
        setupObservers()
        viewModel.loadOrdersBySeller(sellerId)

        val selectedStatus = arguments?.getString("selectedStatus")
        if (selectedStatus != null) {
            val status = OrderStatus.valueOf(selectedStatus)
            val tabIndex = when (status) {
                OrderStatus.PENDING -> 1
                OrderStatus.CONFIRMED -> 2
                OrderStatus.SHIPPING -> 3
                OrderStatus.DELIVERED -> 4
                OrderStatus.CANCELED -> 5
            }
            binding.tabLayoutOrders.getTabAt(tabIndex)?.select()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewOrders.adapter = orderAdapter
    }

    private fun setupTabLayout() {
        binding.tabLayoutOrders.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val status = when (tab?.position) {
                    0 -> null // Tất cả
                    1 -> OrderStatus.PENDING
                    2 -> OrderStatus.CONFIRMED
                    3 -> OrderStatus.SHIPPING
                    4 -> OrderStatus.DELIVERED
                    5 -> OrderStatus.CANCELED
                    else -> null
                }
                viewModel.filterOrdersByStatus(status)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            orderAdapter.submitList(orders)
            binding.textViewNoOrders.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}