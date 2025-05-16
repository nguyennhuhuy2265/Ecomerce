package com.example.ecommerce.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapter.admin.CategoryAdapter
import com.example.ecommerce.databinding.AdminFragmentCategoriesBinding
import com.example.ecommerce.model.Category
import com.example.ecommerce.repository.OperationStatus
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.viewmodel.admin.CategoryViewModel

class CategoriesFragment : Fragment() {
    private var _binding: AdminFragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels {
        CategoryViewModel.Factory(CategoryRepository())
    }
    private lateinit var adapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdminFragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Khởi tạo adapter và RecyclerView
        adapter = CategoryAdapter(emptyList()) { category ->
            showDeleteConfirmationDialog(category)
        }
        binding.recyclerViewCategories.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@CategoriesFragment.adapter
        }

        // Quan sát danh sách danh mục
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.updateCategories(categories)
        }

        // Quan sát trạng thái thao tác
        viewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                is OperationStatus.Success -> {
                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                    viewModel.fetchCategories() // Cập nhật lại danh sách sau khi thêm/xóa
                }
                is OperationStatus.Error -> {
                    Toast.makeText(context, status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Xử lý nút Thêm
        binding.buttonAddCategory.setOnClickListener {
            val categoryName = binding.editTextCategoryName.text?.toString()?.trim() ?: ""
            if (categoryName.isNotEmpty()) {
                val category = Category(name = categoryName)
                viewModel.addCategory(category)
                binding.editTextCategoryName.text?.clear()
            } else {
                Toast.makeText(context, "Vui lòng nhập tên danh mục", Toast.LENGTH_SHORT).show()
            }
        }

        // Lấy danh sách danh mục ban đầu
        viewModel.fetchCategories()
    }

    // Hiển thị dialog xác nhận xóa
    private fun showDeleteConfirmationDialog(category: Category) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa danh mục '${category.name}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                category.id?.let { viewModel.deleteCategory(it) }
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}