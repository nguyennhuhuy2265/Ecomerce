package com.example.ecommerce.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapter.admin.AdminProductAdapter
import com.example.ecommerce.databinding.AdminSellerProductsActivityBinding
import com.example.ecommerce.viewmodel.admin.AdminViewModel

class SellerProductsFragment : Fragment() {
    private var _binding: AdminSellerProductsActivityBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var productAdapter: AdminProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdminSellerProductsActivityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sellerId = arguments?.getString("SELLER_ID") ?: return
        val sellerName = arguments?.getString("SELLER_NAME") ?: "Seller"
        (activity as? AdminActivity)?.supportActionBar?.title = "Sản phẩm của $sellerName"

        productAdapter = AdminProductAdapter { productId ->
            viewModel.deleteProduct(productId)
        }
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { success ->
            Toast.makeText(context, if (success) "Xóa sản phẩm thành công" else "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show()
            if (success) viewModel.fetchProductsBySeller(sellerId)
        }

        viewModel.fetchProductsBySeller(sellerId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}