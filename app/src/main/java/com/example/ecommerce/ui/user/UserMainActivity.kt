package com.example.ecommerce.ui.user

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ecommerce.AccountFragment
import com.example.ecommerce.CategoryFragment
import com.example.ecommerce.HomeFragment
import com.example.ecommerce.NotificationFragment
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityUserMainBinding
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
    }

    private fun setupTopBar() {
        // Thiết lập xử lý tìm kiếm
        binding.userTopBar.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchText = v.text.toString().trim()
                if (searchText.isNotEmpty()) {
                    Toast.makeText(this, "Đang tìm kiếm: $searchText", Toast.LENGTH_SHORT).show()
                    // TODO: Xử lý tìm kiếm
                }
                true
            } else {
                false
            }
        }

        // Thiết lập sự kiện click cho giỏ hàng
        binding.userTopBar.layoutCart.setOnClickListener {
            Toast.makeText(this, "Đang mở giỏ hàng", Toast.LENGTH_SHORT).show()
            // TODO: Mở màn hình giỏ hàng
        }

        // Thiết lập sự kiện click cho tin nhắn
        binding.userTopBar.layoutMessage.setOnClickListener {
            Toast.makeText(this, "Đang mở tin nhắn", Toast.LENGTH_SHORT).show()
            // TODO: Mở màn hình tin nhắn
        }

        // Demo hiển thị badge
        updateCartBadge(7)
        updateMessageBadge(5)
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    private fun updateCartBadge(count: Int) {
        binding.userTopBar.tvCartBadge.apply {
            text = count.toString()
            isVisible = count > 0
        }
    }

    // Cập nhật số lượng tin nhắn mới
    private fun updateMessageBadge(count: Int) {
        binding.userTopBar.tvMessageBadge.apply {
            text = count.toString()
            isVisible = count > 0
        }
    }
}