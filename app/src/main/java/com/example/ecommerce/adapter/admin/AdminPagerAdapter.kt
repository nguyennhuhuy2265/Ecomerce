package com.example.ecommerce.adapter.admin

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ecommerce.ui.admin.AdminActivity
//import com.example.ecommerce.ui.admin.AdsFragment
import com.example.ecommerce.ui.admin.CategoriesFragment
import com.example.ecommerce.ui.admin.UsersFragment

class AdminPagerAdapter(fragment: AdminActivity) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UsersFragment()

            1 -> CategoriesFragment()
//            2 -> AdsFragment()
            else -> UsersFragment()
        }
    }
}