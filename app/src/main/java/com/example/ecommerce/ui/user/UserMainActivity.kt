package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.AccountFragment
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserActivityMainBinding
import com.example.ecommerce.viewmodel.user.UserMainViewModel

class UserMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = UserActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModel: UserMainViewModel by viewModels()

        // Thiết lập navigation dưới cùng
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

        viewModel.selectedTab.observe(this) { tab ->
            val fragment = when (tab) {
                UserMainViewModel.Tab.HOME -> HomeFragment()
                UserMainViewModel.Tab.CATEGORY -> CategoryFragment()
                UserMainViewModel.Tab.NOTIFICATION -> NotificationFragment()
                UserMainViewModel.Tab.ACCOUNT -> AccountFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit()
        }

        // Thiết lập thanh trên cùng
        with(binding.topBarUser) {
            etSearch.setOnClickListener { startActivity(Intent(this@UserMainActivity, SearchActivity::class.java)) }
            flCart.setOnClickListener { startActivity(Intent(this@UserMainActivity, CartActivity::class.java)) }
            flChat.setOnClickListener { startActivity(Intent(this@UserMainActivity, ChatActivity::class.java)) }
        }

        // Mặc định mở tab Home khi khởi động
        if (savedInstanceState == null) {
            viewModel.selectTab(UserMainViewModel.Tab.HOME)
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }
}