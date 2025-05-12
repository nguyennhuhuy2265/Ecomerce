package com.example.ecommerce.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.databinding.AdminActivityBinding
import com.example.ecommerce.viewmodel.admin.AdminViewModel
import com.example.ecommerce.adapter.admin.AdminAdapter
class AdminActivity : AppCompatActivity() {
    private lateinit var binding: AdminActivityBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adminAdapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo RecyclerView
        adminAdapter = AdminAdapter(
            onRoleChange = { userId, newRole -> viewModel.updateUserRole(userId, newRole) },
            onDelete = { userId -> viewModel.deleteUser(userId) }
        )
        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(this@AdminActivity)
            adapter = adminAdapter
        }

        // Quan sát LiveData
        viewModel.users.observe(this) { users ->
            adminAdapter.submitList(users)
        }

        viewModel.operationResult.observe(this) { success ->
            Toast.makeText(this, if (success) "Thành công" else "Thất bại", Toast.LENGTH_SHORT).show()
        }

        // Lấy danh sách người dùng khi khởi động
        viewModel.fetchUsers()
    }
}