package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ProductAdapter
import com.example.ecommerce.databinding.UserActivitySearchBinding
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.user.SearchViewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: UserActivitySearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        setupRecyclerView()
        setupSearchBar()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("product", product)
            }
            startActivity(intent)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = adapter
    }

    private fun setupSearchBar() {
        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.etSearch.text.toString().trim()
                viewModel.searchProducts(query)
                true
            } else {
                false
            }
        }
        binding.imgSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            viewModel.searchProducts(query)
        }
        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.products.observe(this) { products ->
            adapter.updateProducts(products)
            binding.layoutNoResults.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            binding.rvProducts.visibility = if (products.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { error ->
            error?.let { binding.layoutNoResults.visibility = View.VISIBLE }
        }
    }
}