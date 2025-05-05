package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ecommerce.AccountFragment
import com.example.ecommerce.CategoryFragment
import com.example.ecommerce.NotificationFragment
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityUserMainBinding
import com.example.ecommerce.ui.HomeFragment
import com.example.ecommerce.viewmodel.user.UserMainViewModel

class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserMainBinding
    private val viewModel: UserMainViewModel by viewModels()

    // Tạo các Fragment sẵn
    private val fragments = mapOf(
        UserMainViewModel.Tab.HOME to HomeFragment(),
        UserMainViewModel.Tab.CATEGORY to CategoryFragment(),
        UserMainViewModel.Tab.NOTIFICATION to NotificationFragment(),
        UserMainViewModel.Tab.ACCOUNT to AccountFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thiết lập TopBar
        setupTopBar()

        // 1. Khi người dùng chọn tab ở bottom nav
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val tab = when (item.itemId) {
                R.id.nav_home -> UserMainViewModel.Tab.HOME
                R.id.nav_category -> UserMainViewModel.Tab.CATEGORY
                R.id.nav_notification -> UserMainViewModel.Tab.NOTIFICATION
                R.id.nav_account -> UserMainViewModel.Tab.ACCOUNT
                else -> UserMainViewModel.Tab.HOME
            }
            viewModel.selectTab(tab)
            true
        }

        // 2. Quan sát tab thay đổi và đổi Fragment
        viewModel.selectedTab.observe(this) { tab ->
            val fragment = fragments[tab] ?: return@observe
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit()
        }

        // Đặt tab mặc định
        if (savedInstanceState == null) {
            viewModel.selectTab(UserMainViewModel.Tab.HOME)
        }
    }

    private fun setupTopBar() {
        // Khi click vào thanh tìm kiếm
        binding.topBarUser.etSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Khi click giỏ hàng
        binding.topBarUser.flCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Khi click tin nhắn
        binding.topBarUser.flChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // Demo hiển thị badge
        updateCartBadge(7)
        updateMessageBadge(5)
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    private fun updateCartBadge(count: Int) {
        binding.topBarUser.tvCartBadge.apply {
            text = count.toString()
            isVisible = count > 0
        }
    }

    // Cập nhật số lượng tin nhắn mới
    private fun updateMessageBadge(count: Int) {
        binding.topBarUser.tvChatBadge.apply {
            text = count.toString()
            isVisible = count > 0
        }
    }

    override fun onBackPressed() {
        // Kiểm tra nếu có Fragment trong back stack thì pop
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}