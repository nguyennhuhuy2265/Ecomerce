package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserMainViewModel : ViewModel() {

    // Định nghĩa các tab
    enum class Tab {
        HOME,
        CATEGORY,
        NOTIFICATION,
        ACCOUNT
    }
    // LiveData tab đang chọn, mặc định là HOME
    private val _selectedTab = MutableLiveData(Tab.HOME)
    val selectedTab: LiveData<Tab> = _selectedTab

    // Gọi để đổi tab
    fun selectTab(tab: Tab) {
        _selectedTab.value = tab
    }
}
