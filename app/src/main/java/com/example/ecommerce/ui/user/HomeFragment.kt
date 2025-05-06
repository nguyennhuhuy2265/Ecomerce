package com.example.ecommerce.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerce.R
import com.example.ecommerce.adapter.BannerAdapter
import com.example.ecommerce.adapter.ProductAdapter
import com.example.ecommerce.databinding.FragmentUserHomeBinding
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.ui.component.GridSpacingItemDecoration
import com.example.ecommerce.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var featuredAdapter: ProductAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupBanner()
        setupScrollListener()
        observeData()
        viewModel.fetchBanners()
        viewModel.fetchFeaturedProducts()
    }

    private fun setupRecyclerView() {
        featuredAdapter = ProductAdapter(emptyList()) { product ->
            navigateToProductDetail(product)
        }
        binding.rvFeaturedProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = featuredAdapter
            setHasFixedSize(true)
            val spacingInPixels = 16
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))
        }
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter(emptyList())
        binding.vpBanner.apply {
            adapter = bannerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
        }
        val bannerRunnable = object : Runnable {
            override fun run() {
                if (_binding != null && bannerAdapter.itemCount > 0) {
                    val currentItem = binding.vpBanner.currentItem
                    val nextItem = if (currentItem == bannerAdapter.itemCount - 1) 0 else currentItem + 1
                    binding.vpBanner.currentItem = nextItem
                    bannerHandler.postDelayed(this, 3000)
                }
            }
        }
        bannerHandler.postDelayed(bannerRunnable, 3000)
    }

    private fun setupScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val contentView = binding.nestedScrollView.getChildAt(0)
            val totalHeight = contentView.height
            val scrollViewHeight = binding.nestedScrollView.height
            Log.d(TAG, "onScroll: scrollY=$scrollY, totalHeight=$totalHeight, scrollViewHeight=$scrollViewHeight, hasMoreData=${viewModel.hasMoreData.value}")
            if (scrollY + scrollViewHeight >= totalHeight - 500 && viewModel.hasMoreData.value == true) {
                Log.d(TAG, "Loading more products triggered")
                viewModel.loadMoreFeaturedProducts()
            }
        }
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.updateBanners(banners)
        }
        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            Log.d(TAG, "observeData: Updated products, total=${products.size}")
            featuredAdapter.updateProducts(products)
        }
    }

    private fun navigateToProductDetail(product: Product) {
        val fragment = ProductDetailFragment()
        val bundle = Bundle().apply {
            putParcelable("product", product)
        }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        bannerHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}