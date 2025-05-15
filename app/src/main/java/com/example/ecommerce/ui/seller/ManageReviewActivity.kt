package com.example.ecommerce.ui.seller

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ReviewAdapter
import com.example.ecommerce.databinding.SellerActivityManageReviewBinding
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.seller.ManageReviewViewModel

class ManageReviewActivity : AppCompatActivity() {
    private lateinit var binding: SellerActivityManageReviewBinding
    private lateinit var viewModel: ManageReviewViewModel
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SellerActivityManageReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ManageReviewViewModel::class.java)

        // Lấy product từ Intent
        val product = intent.getParcelableExtra<Product>("product")
        product?.let { viewModel.loadReviews(it.id) }

        setupToolbar()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        reviewAdapter = ReviewAdapter()
        binding.rvProductReviews.layoutManager = LinearLayoutManager(this)
        binding.rvProductReviews.adapter = reviewAdapter
    }

    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            product?.let {
                binding.tvProductName.text = it.name
                binding.tvProductPrice.text = "120.000đ" // Giả lập, có thể dùng formatter
                binding.tvSoldCount.text = "Đã bán: ${it.soldCount}"
                Glide.with(this)
                    .load(it.imageUrls.firstOrNull() ?: R.drawable.ic_load)
                    .into(binding.ivProductImage)
            }
        }

        viewModel.reviews.observe(this) { reviews ->
            reviewAdapter.submitList(reviews)
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}