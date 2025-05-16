package com.example.ecommerce.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.admin.AdminAdapter
import com.example.ecommerce.databinding.AdminFragmentUsersBinding
import com.example.ecommerce.viewmodel.admin.AdminViewModel

class UsersFragment : Fragment() {
    private var _binding: AdminFragmentUsersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var adminAdapter: AdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AdminFragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminAdapter = AdminAdapter(
            onRoleChange = { userId, newRole -> viewModel.updateUserRole(userId, newRole) },
            onDelete = { userId -> viewModel.deleteUser(userId) },
            onSellerClick = { user ->
                if (user.role == "seller") {
                    val fragment = SellerProductsFragment().apply {
                        arguments = Bundle().apply {
                            putString("SELLER_ID", user.id)
                            putString("SELLER_NAME", user.shopName ?: user.name)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
        )
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewUsers.adapter = adminAdapter

        viewModel.users.observe(viewLifecycleOwner) { users ->
            adminAdapter.submitList(users)
        }

        viewModel.operationResult.observe(viewLifecycleOwner) { success ->
            Toast.makeText(context, if (success) "Thành công" else "Thất bại", Toast.LENGTH_SHORT).show()
        }

        viewModel.fetchUsers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}