package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SellerMainViewModel : ViewModel() {

    // Định nghĩa các tab
    enum class Tab {
        DASHBOARD,
        PRODUCT,
        ODER,
        NOTIFICATION,
        SHOP
    }
    // LiveData tab đang chọn, mặc định là HOME
    private val _selectedTab = MutableLiveData(Tab.DASHBOARD)
    val selectedTab: LiveData<Tab> = _selectedTab

    // Gọi để đổi tab
    fun selectTab(tab: Tab) {
        _selectedTab.value = tab
    }
}
