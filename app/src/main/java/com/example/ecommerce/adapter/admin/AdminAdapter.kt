package com.example.ecommerce.adapter.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.User

class AdminAdapter(
    private val onRoleChange: (String, String) -> Unit,
    private val onDelete: (String) -> Unit,
    private val onSellerClick: (User) -> Unit
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
        holder.phoneTextView.text = user.phoneNumber ?: "Không có số"

        val roles = arrayOf("seller", "user")
        val adapter = ArrayAdapter(
            holder.itemView.context,
            android.R.layout.simple_dropdown_item_1line,
            roles
        )
        holder.roleAutoComplete.setAdapter(adapter)

        val roleIndex = roles.indexOf(user.role).takeIf { it >= 0 } ?: 0
        holder.roleAutoComplete.setText(roles[roleIndex], false)

        holder.roleAutoComplete.setOnItemClickListener { _, _, pos, _ ->
            val newRole = roles[pos]
            if (newRole != user.role) {
                onRoleChange(user.id, newRole)
            }
        }

        holder.deleteButton.setOnClickListener {
            onDelete(user.id)
        }

        if (user.role == "seller") {
            holder.itemView.setOnClickListener {
                onSellerClick(user)
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