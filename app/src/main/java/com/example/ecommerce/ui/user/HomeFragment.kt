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
import androidx.viewpager2.widget.ViewPager2
import com.example.ecommerce.R
import com.example.ecommerce.adapter.BannerAdapter
import com.example.ecommerce.adapter.ProductAdapter
import com.example.ecommerce.databinding.FragmentUserHomeBinding
import com.example.ecommerce.model.Product
import com.example.ecommerce.ui.component.GridSpacingItemDecoration
import com.example.ecommerce.ui.viewmodel.HomeViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var featuredAdapter: ProductAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bannerAdapter = BannerAdapter(emptyList())
        featuredAdapter = ProductAdapter(emptyList()) { product ->
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
            adapter = featuredAdapter
            setHasFixedSize(true)
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        }

        val bannerRunnable = object : Runnable {
            override fun run() {
                if (bannerAdapter.itemCount > 0) {
                    binding.vpBanner.currentItem = (binding.vpBanner.currentItem + 1) % bannerAdapter.itemCount
                }
                bannerHandler.postDelayed(this, 3000)
            }
        }
        bannerHandler.postDelayed(bannerRunnable, 3000)

        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val contentView = binding.nestedScrollView.getChildAt(0)
            if (scrollY + binding.nestedScrollView.height >= contentView.height - 500 && viewModel.hasMoreData.value == true) {
                viewModel.loadMoreFeaturedProducts()
            }
        }

        viewModel.banners.observe(viewLifecycleOwner) { bannerAdapter.updateBanners(it) }
        viewModel.featuredProducts.observe(viewLifecycleOwner) { featuredAdapter.updateProducts(it) }
        viewModel.fetchBanners()
        viewModel.fetchFeaturedProducts()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFeaturedProducts() // Làm mới sản phẩm khi quay lại tab
    }

    override fun onDestroyView() {
        bannerHandler.removeCallbacksAndMessages(null)
        super.onDestroyView()
        _binding = null
    }
}