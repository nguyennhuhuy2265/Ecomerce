package com.example.ecommerce.ui.user

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.ecommerce.R

class UserMainActivity : AppCompatActivity() {

    private lateinit var homeTab: LinearLayout
    private lateinit var categoryTab: LinearLayout
    private lateinit var notificationTab: LinearLayout
    private lateinit var accountTab: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_main)

//        // Khởi tạo view
//        homeTab = findViewById(R.id.home_tab)
//        categoryTab = findViewById(R.id.category_tab)
//        notificationTab = findViewById(R.id.notification_tab)
//        accountTab = findViewById(R.id.account_tab)
//
//        // Cài đặt sự kiện
//        setupTabClickListeners()
//
//        // Mặc định mở HomeFragment
//        loadFragment(HomeFragment())
//        updateTabUI(homeTab)
    }

//    private fun setupTabClickListeners() {
//        homeTab.setOnClickListener {
//            loadFragment(HomeFragment())
//            updateTabUI(homeTab)
//        }
//
//        categoryTab.setOnClickListener {
//            loadFragment(CategoryFragment())
//            updateTabUI(categoryTab)
//        }
//
//        notificationTab.setOnClickListener {
//            loadFragment(NotificationFragment())
//            updateTabUI(notificationTab)
//        }
//
//        accountTab.setOnClickListener {
//            loadFragment(AccountFragment())
//            updateTabUI(accountTab)
//        }
//    }
//
//    private fun loadFragment(fragment: Fragment) {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commit()
//    }
//
//    private fun updateTabUI(selectedTab: View) {
//        resetAllTabs()
//
//        val primaryColor = ContextCompat.getColor(this, R.color.primary)
//
//        when (selectedTab) {
//            homeTab -> setTabActive(homeTab, R.id.home_icon, R.id.home_text, primaryColor)
//            categoryTab -> setTabActive(categoryTab, R.id.category_icon, R.id.category_text, primaryColor)
//            notificationTab -> setTabActive(notificationTab, R.id.notification_icon, R.id.notification_text, primaryColor)
//            accountTab -> setTabActive(accountTab, R.id.account_icon, R.id.account_text, primaryColor)
//        }
//    }
//
//    private fun resetAllTabs() {
//        val greyColor = ContextCompat.getColor(this, R.color.gray_dark)
//        setTabInactive(homeTab, R.id.home_icon, R.id.home_text, greyColor)
//        setTabInactive(categoryTab, R.id.category_icon, R.id.category_text, greyColor)
//        setTabInactive(notificationTab, R.id.notification_icon, R.id.notification_text, greyColor)
//        setTabInactive(accountTab, R.id.account_icon, R.id.account_text, greyColor)
//    }
//
//    private fun setTabActive(tab: LinearLayout, iconId: Int, textId: Int, color: Int) {
//        tab.findViewById<ImageView>(iconId).setColorFilter(color)
//        tab.findViewById<TextView>(textId).setTextColor(color)
//    }
//
//    private fun setTabInactive(tab: LinearLayout, iconId: Int, textId: Int, color: Int) {
//        tab.findViewById<ImageView>(iconId).setColorFilter(color)
//        tab.findViewById<TextView>(textId).setTextColor(color)
//    }
}
