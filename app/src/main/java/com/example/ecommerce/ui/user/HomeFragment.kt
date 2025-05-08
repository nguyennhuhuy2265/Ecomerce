package com.example.ecommerce.ui.user

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
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.CloudinaryRepository
import com.example.ecommerce.ui.component.GridSpacingItemDecoration
import com.example.ecommerce.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var featuredAdapter: ProductAdapter
    private var bannerJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
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
        featuredAdapter = ProductAdapter(
            emptyList(),
            { product -> navigateToProductDetail(product) }
        )
        binding.rvFeaturedProducts.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = featuredAdapter
            setHasFixedSize(true)
            addItemDecoration(GridSpacingItemDecoration(2, 16, true))
        }
    }

    private fun setupBanner() {
        bannerAdapter = BannerAdapter(emptyList())
        binding.vpBanner.apply {
            adapter = bannerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1 // Giới hạn số trang giữ trong bộ nhớ
        }
        bannerJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (bannerAdapter.itemCount > 0) {
                    val currentItem = binding.vpBanner.currentItem
                    val nextItem = if (currentItem == bannerAdapter.itemCount - 1) 0 else currentItem + 1
                    binding.vpBanner.currentItem = nextItem
                }
                kotlinx.coroutines.delay(3000)
            }
        }
    }

    private fun setupScrollListener() {
        binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val contentView = binding.nestedScrollView.getChildAt(0)
            if (scrollY + binding.nestedScrollView.height >= contentView.height - 500 && viewModel.hasMoreData.value == true) {
                viewModel.loadMoreFeaturedProducts()
            }
        }
    }

    private fun observeData() {
        viewModel.banners.observe(viewLifecycleOwner) { banners ->
            bannerAdapter.updateBanners(banners)
        }
        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            featuredAdapter.updateProducts(products)
        }
    }

    private fun navigateToProductDetail(product: Product) {
        val fragment = ProductDetailFragment()
        val bundle = Bundle().apply { putParcelable("product", product) }
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.flFragment, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        bannerJob?.cancel() // Hủy coroutine khi fragment bị hủy
        super.onDestroyView()
        _binding = null
    }
}