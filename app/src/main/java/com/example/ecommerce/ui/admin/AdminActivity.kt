package com.example.ecommerce.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerce.adapter.admin.AdminPagerAdapter
import com.example.ecommerce.databinding.AdminActivityBinding
import com.example.ecommerce.ui.common.LoginActivity
import com.example.ecommerce.viewmodel.admin.AdminViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class AdminActivity : AppCompatActivity() {
    private lateinit var binding: AdminActivityBinding
    private val viewModel: AdminViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val pagerAdapter = AdminPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Người dùng"
//                1 -> "Quảng cáo"
                1 -> "Danh mục"
                else -> ""
            }
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.fetchUsers()
            }
        })

        // Xử lý logout
        binding.tabLogout.setOnClickListener {
            // Thêm logic logout (ví dụ: quay về màn hình đăng nhập)
            startActivity(Intent(this, LoginActivity::class.java)) // Giả lập
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}