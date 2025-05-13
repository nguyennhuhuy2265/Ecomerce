package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemProductCheckoutBinding
import com.example.ecommerce.model.Cart

class CheckoutItemAdapter : RecyclerView.Adapter<CheckoutItemAdapter.CheckoutItemViewHolder>() {

    private var items = emptyList<Cart>()

    fun submitList(newItems: List<Cart>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutItemViewHolder {
        val binding = UserItemProductCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CheckoutItemViewHolder(private val binding: UserItemProductCheckoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.tvProductName.text = cart.productName
            binding.tvProductOptions.text = if (cart.selectedOptions.isNotEmpty()) {
                "Phân loại: ${cart.selectedOptions.joinToString(", ")}"
            } else {
                "Không có tùy chọn"
            }
            binding.tvProductPrice.text = "₫${(cart.unitPrice * cart.quantity).toInt()}"
            binding.tvProductQuantity.text = "x${cart.quantity}"

            Glide.with(binding.ivProductImage.context)
                .load(cart.productImage)
                .placeholder(R.drawable.ic_load)
                .into(binding.ivProductImage)
        }
    }
}