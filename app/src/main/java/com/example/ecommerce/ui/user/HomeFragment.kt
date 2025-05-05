package com.example.ecommerce.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentUserHomeBinding
import com.example.ecommerce.model.common.Banner
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.ui.user.viewmodel.HomeViewModel


class HomeFragment : Fragment() {
    private var _binding: FragmentUserHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var featuredAdapter: ProductAdapter
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupObservers()
    }

    private fun setupRecyclerViews() {
        binding.rvRecommendedProducts.layoutManager = LinearLayoutManager(
            requireContext(), LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvRecommendedProducts.setHasFixedSize(true)
        binding.rvRecommendedProducts.adapter = ProductAdapter(emptyList()) { product ->
            navigateToProductDetail(product)
        }

        binding.rvFeaturedProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFeaturedProducts.setHasFixedSize(true)
        featuredAdapter = ProductAdapter(emptyList()) { product ->
            navigateToProductDetail(product)
        }
        binding.rvFeaturedProducts.adapter = featuredAdapter

        binding.rvFeaturedProducts.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (!isLoading && totalItemCount <= (lastVisibleItem + 2) && viewModel.hasMoreData.value == true) {
                    isLoading = true
                    viewModel.loadMoreFeaturedProducts()
                }
            }
        })
    }

    private fun setupObservers() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            (binding.rvRecommendedProducts.adapter as ProductAdapter).updateProducts(products)
        }

        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            featuredAdapter.updateProducts(products)
            isLoading = false
        }

        viewModel.banners.observe(viewLifecycleOwner) { banners ->
            if (banners.isNotEmpty()) {
                val banner = banners.first()
                Glide.with(this)
                    .load(banner.imageUrl)
                    .into(binding.ivBanner)
            }
        }

        viewModel.fetchBanners()
        viewModel.fetchRecommendedProducts()
        viewModel.fetchFeaturedProducts()
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
        _binding = null
    }
}