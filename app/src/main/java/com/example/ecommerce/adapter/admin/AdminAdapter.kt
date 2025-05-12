package com.example.ecommerce.adapter.admin

import android.content.Intent
import com.example.ecommerce.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.model.User
import com.example.ecommerce.ui.admin.AdminSellerProductsActivity

class AdminAdapter(
    private val onRoleChange: (String, String) -> Unit,
    private val onDelete: (String) -> Unit
) : ListAdapter<User, AdminAdapter.AdminViewHolder>(UserDiffCallback()) {

    class AdminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textViewName)
        val emailTextView: TextView = itemView.findViewById(R.id.textViewEmail)
        val phoneTextView: TextView = itemView.findViewById(R.id.textViewPhone)
        val roleAutoComplete: AutoCompleteTextView = itemView.findViewById(R.id.spinnerRole)
        val deleteButton: Button = itemView.findViewById(R.id.buttonDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_item_user, parent, false)
        return AdminViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        val user = getItem(position)
        holder.nameTextView.text = user.name
        holder.emailTextView.text = user.email
        holder.phoneTextView.text = user.phoneNumber

        // Thiết lập AutoCompleteTextView cho vai trò
        val roles = arrayOf("seller", "user")
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )
        holder.roleAutoComplete.setAdapter(adapter)

        // Chọn vai trò hiện tại
        val roleIndex = roles.indexOf(user.role).takeIf { it >= 0 } ?: 0
        holder.roleAutoComplete.setText(roles[roleIndex], false)

        // Xử lý thay đổi vai trò
        holder.roleAutoComplete.setOnItemClickListener { _, _, pos, _ ->
            val newRole = roles[pos]
            if (newRole != user.role) {
                onRoleChange(user.id, newRole)
            }
        }

        // Xử lý xóa người dùng
        holder.deleteButton.setOnClickListener {
            onDelete(user.id)
        }

        // Xử lý nhấn vào mục người dùng có role = seller
        if (user.role == "seller") {
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, AdminSellerProductsActivity::class.java).apply {
                    putExtra("SELLER_ID", user.id)
                    putExtra("SELLER_NAME", user.name)
                }
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.itemView.setOnClickListener(null)
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean = oldItem == newItem
}