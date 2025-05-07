package com.example.ecommerce.ui.seller

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivitySellerMainBinding
import com.example.ecommerce.viewmodel.seller.SellerMainViewModel

class SellerMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySellerMainBinding
    private val viewModel: SellerMainViewModel by viewModels()

    // Tạo các Fragment sẵn
    private val fragments = mapOf(
        SellerMainViewModel.Tab.PRODUCT to ProductFragment(),
        SellerMainViewModel.Tab.ODER to OderFragment(),
        SellerMainViewModel.Tab.NOTIFICATION to NotificationFragment(),
        SellerMainViewModel.Tab.SHOP to ShopFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        // Thiết lập TopBar
//        setupTopBar()

        // 1. Khi người dùng chọn tab ở bottom nav
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val tab = when (item.itemId) {
                R.id.nav_product -> SellerMainViewModel.Tab.PRODUCT
                R.id.nav_category -> SellerMainViewModel.Tab.ODER
                R.id.nav_notification -> SellerMainViewModel.Tab.NOTIFICATION
                R.id.nav_account -> SellerMainViewModel.Tab.SHOP
                else -> SellerMainViewModel.Tab.PRODUCT
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
            viewModel.selectTab(SellerMainViewModel.Tab.PRODUCT)
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