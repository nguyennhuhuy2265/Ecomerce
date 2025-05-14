package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserFragmentAccountBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.ui.common.LoginActivity
import com.example.ecommerce.viewmodel.user.AccountViewModel
import kotlin.text.ifEmpty

class AccountFragment : Fragment() {

    private var _binding: UserFragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvUsername.text = it.name
                binding.tvUserEmail.text = it.email
                Glide.with(this)
                    .load(user.avatarUrl?.ifEmpty { null } ?: R.drawable.ic_load)
                    .placeholder(R.drawable.ic_load)
                    .into(binding.ivUserAvatar)
            }
        }

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            updateOrderCounts(orders)
        }
    }

    private fun updateOrderCounts(orders: List<Order>) {
        val waitingConfirm = orders.count { it.status == OrderStatus.PENDING }
        val waitingPickup = orders.count { it.status == OrderStatus.CONFIRMED }
        val waitingDelivery = orders.count { it.status == OrderStatus.SHIPPING }
        val delivered = orders.count { it.status == OrderStatus.DELIVERED }

        binding.tvWaitingConfirmCount.apply {
            text = waitingConfirm.toString()
            visibility = if (waitingConfirm > 0) View.VISIBLE else View.GONE
        }
        binding.tvWaitingPickupCount.apply {
            text = waitingPickup.toString()
            visibility = if (waitingPickup > 0) View.VISIBLE else View.GONE
        }
        binding.tvWaitingDeliveryCount.apply {
            text = waitingDelivery.toString()
            visibility = if (waitingDelivery > 0) View.VISIBLE else View.GONE
        }
        binding.tvDeliveredCount.apply {
            text = delivered.toString()
            visibility = if (delivered > 0) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        binding.tvViewOrderHistory.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java))
        }

        binding.layoutWaitingConfirm.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java).apply {
                putExtra("status", OrderStatus.PENDING.name)
            })
        }
        binding.layoutWaitingPickup.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java).apply {
                putExtra("status", OrderStatus.CONFIRMED.name)
            })
        }
        binding.layoutWaitingDelivery.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java).apply {
                putExtra("status", OrderStatus.SHIPPING.name)
            })
        }
        binding.layoutDelivered.setOnClickListener {
            startActivity(Intent(requireContext(), OrderActivity::class.java).apply {
                putExtra("status", OrderStatus.DELIVERED.name)
            })
        }

        binding.layoutSettings.setOnClickListener {
            startActivity(Intent(requireContext(), EditAccountActivity::class.java))
        }

        binding.layoutLogout.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}