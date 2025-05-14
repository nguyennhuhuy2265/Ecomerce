package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapter.seller.NotificationAdapter
import com.example.ecommerce.databinding.UserFragmentNotificationBinding
import com.example.ecommerce.repository.NotificationRepository
import com.example.ecommerce.viewmodel.seller.NotificationViewModel
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NotificationFragment : Fragment() {

    private var _binding: UserFragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotificationViewModel by viewModels()
    private lateinit var adapter: NotificationAdapter
    private val notificationRepository = NotificationRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            println("User not authenticated")
            return
        }
        println("NotificationFragment onViewCreated, userId: $userId")

        adapter = NotificationAdapter { notification ->
            viewModel.markAsRead(notification)
            notification.orderId?.let { orderId ->
                val intent = Intent(requireContext(), OrderDetailActivity::class.java).apply {
                    putExtra("orderId", orderId)
                }
                startActivity(intent)
            }
        }

        binding.recyclerViewOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewOrders.adapter = adapter

        viewModel.unreadNotifications.observe(viewLifecycleOwner) { notifications ->
            println("Unread notifications updated: ${notifications.size}")
            if (binding.tabLayoutNotifications.selectedTabPosition == 0) {
                adapter.submitList(notifications)
            }
        }

        viewModel.readNotifications.observe(viewLifecycleOwner) { notifications ->
            println("Read notifications updated: ${notifications.size}")
            if (binding.tabLayoutNotifications.selectedTabPosition == 1) {
                adapter.submitList(notifications)
            }
        }

        binding.tabLayoutNotifications.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> viewModel.unreadNotifications.value?.let { adapter.submitList(it) }
                    1 -> viewModel.readNotifications.value?.let { adapter.submitList(it) }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tabLayoutNotifications.getTabAt(0)?.select()
        viewModel.loadNotifications(userId)

        viewModel.error.observe(viewLifecycleOwner) { error ->
            println("Error in NotificationFragment: $error")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}