package com.example.ecommerce.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapter.user.OrderAdapter
import com.example.ecommerce.databinding.UserFragmentOrderBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.viewmodel.user.AccountViewModel
import com.google.android.material.tabs.TabLayout

class OrderFragment : Fragment() {

    private var _binding: UserFragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by activityViewModels()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderAdapter = OrderAdapter(emptyList()) { order ->
            // Chuyển đến chi tiết đơn hàng nếu cần
            // Ví dụ: startActivity(Intent(context, OrderDetailActivity::class.java).apply { putExtra("orderId", order.id) })
        }

        setupRecyclerView()
        setupTabLayout()
        setupObservers()

        // Chọn tab mặc định nếu có trạng thái từ Intent
        val selectedStatus = arguments?.getString("status")
        selectedStatus?.let {
            val status = OrderStatus.valueOf(it)
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
                viewModel.orders.value?.let { orders ->
                    filterOrdersByStatus(status, orders)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupObservers() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            val status = when (binding.tabLayoutOrders.selectedTabPosition) {
                0 -> null
                1 -> OrderStatus.PENDING
                2 -> OrderStatus.CONFIRMED
                3 -> OrderStatus.SHIPPING
                4 -> OrderStatus.DELIVERED
                5 -> OrderStatus.CANCELED
                else -> null
            }
            filterOrdersByStatus(status, orders)
        }
    }

    private fun filterOrdersByStatus(status: OrderStatus?, orders: List<Order>) {
        val filteredOrders = if (status == null) orders else orders.filter { it.status == status }
        orderAdapter.updateOrders(filteredOrders)
        binding.textViewNoOrders.visibility = if (filteredOrders.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}