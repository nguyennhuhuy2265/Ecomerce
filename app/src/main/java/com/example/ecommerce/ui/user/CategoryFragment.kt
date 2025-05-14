package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ProductAdapter
import com.example.ecommerce.databinding.UserFragmentCategoryBinding
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.user.CategoryViewModel

class CategoryFragment : Fragment() {

    private var _binding: UserFragmentCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChipGroup()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java).apply {
                putExtra("product", product)
            }
            startActivity(intent)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProducts.adapter = adapter
    }

    private fun setupChipGroup() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.categoryChipGroup.removeAllViews()
            categories.forEach { category ->
                val chip = createChip(category)
                binding.categoryChipGroup.addView(chip)
            }
        }

        // Quan sát danh mục được chọn để cập nhật trạng thái chip
        viewModel.selectedCategoryId.observe(viewLifecycleOwner) { selectedCategoryId ->
            binding.categoryChipGroup.children.forEach { view ->
                val chip = view as com.google.android.material.chip.Chip
                chip.isChecked = chip.tag == selectedCategoryId
            }
        }
    }

    private fun createChip(category: Category): com.google.android.material.chip.Chip {
        val chip = com.google.android.material.chip.Chip(context).apply {
            text = category.name
            tag = category.id // Lưu category.id vào tag để so sánh khi cập nhật trạng thái
            isCheckable = true
            setChipBackgroundColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_background_color))
            setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.chip_text_color))
            setOnClickListener {
                binding.categoryChipGroup.check(id)
                viewModel.selectCategory(category.id)
            }
        }
        return chip
    }

    private fun setupObservers() {
        viewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.updateProducts(products)
            binding.layoutEmpty.visibility = if (products.isEmpty()) View.VISIBLE else View.GONE
            binding.rvProducts.visibility = if (products.isNotEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let { binding.layoutEmpty.visibility = View.VISIBLE }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}