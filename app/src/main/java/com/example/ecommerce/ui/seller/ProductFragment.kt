package com.example.ecommerce.ui.seller

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.seller.ProductAdapter
import com.example.ecommerce.databinding.SellerFragmentProductBinding
import com.example.ecommerce.viewmodel.seller.ProductViewModel

class ProductFragment : Fragment() {
    private var _binding: SellerFragmentProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SellerFragmentProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo ProductAdapter
        productAdapter = ProductAdapter(
            products = emptyList(),
            onEdit = { product ->
                // Chuyển đến EditProductActivity để chỉnh sửa sản phẩm
                val intent = Intent(requireContext(), EditProductActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            },
            onDelete = { product ->
                // Xóa sản phẩm
                product.id?.let { productId ->
                    viewModel.deleteProduct(productId)
                } ?: run {
                    Toast.makeText(requireContext(), "Không thể xóa sản phẩm: ID không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Cấu hình RecyclerView
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
            setHasFixedSize(true)
        }

        // Quan sát danh sách sản phẩm đã lọc
        viewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.updateProducts(products)
            binding.tvProductCount.text = "${products.size} sản phẩm"
        }

        // Quan sát kết quả xóa sản phẩm
        viewModel.deleteProductSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Xóa sản phẩm thành công", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.deleteProductError.observe(viewLifecycleOwner) { error ->
            Toast.makeText(requireContext(), "Xóa sản phẩm thất bại: $error", Toast.LENGTH_SHORT).show()
        }

        // Tìm kiếm sản phẩm
        binding.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchProducts(s.toString())
            }
        })

        // Lọc sản phẩm
        binding.filterAll.setOnClickListener {
            updateFilterUI(binding.filterAll, binding.filterActive, binding.filterOutOfStock)
            viewModel.filterProducts("all")
        }
        binding.filterActive.setOnClickListener {
            updateFilterUI(binding.filterActive, binding.filterAll, binding.filterOutOfStock)
            viewModel.filterProducts("active")
        }
        binding.filterOutOfStock.setOnClickListener {
            updateFilterUI(binding.filterOutOfStock, binding.filterAll, binding.filterActive)
            viewModel.filterProducts("out_of_stock")
        }

        // Sắp xếp sản phẩm
        binding.layoutSort.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), binding.layoutSort)
            popupMenu.menuInflater.inflate(R.menu.seller_sort_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.sort_newest -> {
                        binding.tvSortOption.text = "Mới nhất"
                        viewModel.sortProducts("newest")
                    }
                    R.id.sort_price_asc -> {
                        binding.tvSortOption.text = "Giá tăng dần"
                        viewModel.sortProducts("price_asc")
                    }
                    R.id.sort_price_desc -> {
                        binding.tvSortOption.text = "Giá giảm dần"
                        viewModel.sortProducts("price_desc")
                    }
                    R.id.sort_sold_desc -> {
                        binding.tvSortOption.text = "Bán chạy"
                        viewModel.sortProducts("sold_desc")
                    }
                }
                true
            }
            popupMenu.show()
        }

        // Thêm sản phẩm mới
        binding.btnAddNewProduct.setOnClickListener {
            val intent = Intent(requireContext(), AddProductActivity::class.java)
            startActivity(intent)
        }

        // Tải danh sách sản phẩm ban đầu
        viewModel.fetchProducts()
    }

    private fun updateFilterUI(selected: View, vararg others: View) {
        (selected as TextView).apply {
            setBackgroundResource(R.drawable.bg_filter_selected)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        }
        others.forEach { view ->
            (view as TextView).apply {
                setBackgroundResource(R.drawable.bg_filter_normal)
                setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}