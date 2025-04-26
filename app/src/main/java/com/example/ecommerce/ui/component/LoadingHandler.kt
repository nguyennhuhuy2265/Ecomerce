package com.example.ecommerce.ui.component

import androidx.fragment.app.FragmentManager

class LoadingHandler(private val fragmentManager: FragmentManager) {

    private var loadingDialog: LoadingDialog? = null

    fun showLoading() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog()
            loadingDialog?.show(fragmentManager, "loading")
        }
    }

    fun hideLoading() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }
}