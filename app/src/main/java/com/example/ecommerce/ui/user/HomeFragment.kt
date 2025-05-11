package com.example.ecommerce.ui.user

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.BannerAdapter
import com.example.ecommerce.adapter.user.ProductAdapter
import com.example.ecommerce.databinding.UserFragmentHomeBinding
import com.example.ecommerce.viewmodel.user.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: UserFragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var productAdapter: ProductAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UserFragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerAdapter = BannerAdapter(emptyList())
        productAdapter = ProductAdapter(emptyList()) { product ->
            val fragment = ProductDetailFragment().apply { arguments = Bundle().apply { putParcelable("product", product) } }
            parentFragmentManager.beginTransaction()
                .replace(R.id.flFragment, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.vpBanner.apply {
            adapter = bannerAdapter
            offscreenPageLimit = 1
        }

        binding.rvFeaturedProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = productAdapter
            setHasFixedSize(true)
        }

        // Tự động cuộn banner mỗi 3 giây
        val bannerRunnable = object : Runnable {
            override fun run() {
                if (bannerAdapter.itemCount > 0) {
                    binding.vpBanner.currentItem = (binding.vpBanner.currentItem + 1) % bannerAdapter.itemCount
                }
                bannerHandler.postDelayed(this, 3000)
            }
        }
        bannerHandler.postDelayed(bannerRunnable, 3000)

        viewModel.banners.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.updateBanners(banners)
        }

        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
        }

        viewModel.fetchBanners()
        viewModel.fetchFeaturedProducts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFeaturedProducts()
    }

    override fun onDestroyView() {
        bannerHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
        _binding = null
    }
}