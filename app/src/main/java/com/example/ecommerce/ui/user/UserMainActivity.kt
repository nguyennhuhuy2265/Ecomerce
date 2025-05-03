package com.example.ecommerce.ui.user

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
}
