package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.ecommerce.AccountFragment
import com.example.ecommerce.CategoryFragment
import com.example.ecommerce.NotificationFragment
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityUserMainBinding
import com.example.ecommerce.viewmodel.user.UserMainViewModel


class UserMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserMainBinding
    private val viewModel: UserMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTopBar()

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
                .replace(R.id.flFragment, fragment as Fragment)
                .commit()
        }

        if (savedInstanceState == null) {
            viewModel.selectTab(UserMainViewModel.Tab.HOME)
        }
    }

    private fun setupTopBar() {
        binding.topBarUser.etSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        binding.topBarUser.flCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.topBarUser.flChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        updateCartBadge(7)
        updateMessageBadge(5)
    }

    private fun updateCartBadge(count: Int) {
        binding.topBarUser.tvCartBadge.apply {
            text = count.toString()
            isVisible = count > 0
        }
    }

    private fun updateMessageBadge(count: Int) {
        binding.topBarUser.tvChatBadge.apply {
            text = count.toString()
            isVisible = count > 0
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