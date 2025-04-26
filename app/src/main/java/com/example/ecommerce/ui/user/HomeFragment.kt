package com.example.ecommerce

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class HomeFragment : Fragment() {
    private var bannerViewPager: ViewPager2? = null
    private var flashSaleRecycler: RecyclerView? = null
    private var productsRecycler: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_user_home, container, false)


        // Khởi tạo các View
//        bannerViewPager = view.findViewById<ViewPager2?>(R.id.banner_viewpager)
//        flashSaleRecycler = view.findViewById<RecyclerView?>(R.id.flash_sale_recycler)
//        productsRecycler = view.findViewById<RecyclerView?>(R.id.products_recycler)


        // Thiết lập Banner
        setupBanner()


        // Thiết lập Flash Sale
        setupFlashSale()


        // Thiết lập Sản phẩm đề xuất
        setupRecommendedProducts()

        return view
    }

    private fun setupBanner() {
        // Thiết lập adapter cho ViewPager2 banner
        // BannerAdapter bannerAdapter = new BannerAdapter(getBannerList());
        // bannerViewPager.setAdapter(bannerAdapter);
    }

    private fun setupFlashSale() {
        // Thiết lập RecyclerView Flash Sale theo chiều ngang
        val layoutManager = LinearLayoutManager(
            getContext(), LinearLayoutManager.HORIZONTAL, false
        )
        flashSaleRecycler!!.setLayoutManager(layoutManager)


        // FlashSaleAdapter flashSaleAdapter = new FlashSaleAdapter(getFlashSaleProducts());
        // flashSaleRecycler.setAdapter(flashSaleAdapter);
    }

    private fun setupRecommendedProducts() {
        // Thiết lập RecyclerView sản phẩm đề xuất theo dạng lưới
        val gridLayoutManager = GridLayoutManager(getContext(), 2)
        productsRecycler!!.setLayoutManager(gridLayoutManager)


        // ProductAdapter productAdapter = new ProductAdapter(getRecommendedProducts());
        // productsRecycler.setAdapter(productAdapter);
    }
}