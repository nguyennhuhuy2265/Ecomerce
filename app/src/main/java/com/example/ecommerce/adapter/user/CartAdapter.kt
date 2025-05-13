package com.example.ecommerce.adapter.user

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemCartBinding
import com.example.ecommerce.model.Cart
import com.example.ecommerce.ui.user.CheckoutActivity

class CartAdapter(
    private val onDeleteClick: (String) -> Unit,
    private val onQuantityChange: (String, Int) -> Unit,
    private val onBuyClick: (Cart) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartList = emptyList<Cart>()

    fun submitList(carts: List<Cart>) {
        cartList = carts
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = UserItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size

    inner class CartViewHolder(private val binding: UserItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cart: Cart) {
            binding.textProductName.text = cart.productName
            binding.textProductOptions.text = if (cart.selectedOptions.isNotEmpty()) {
                "Phân loại: ${cart.selectedOptions.joinToString(", ")}"
            } else {
                "Không có tùy chọn"
            }
            binding.textProductPrice.text = "₫${(cart.unitPrice * cart.quantity).toInt()}"
            binding.textQuantity.text = cart.quantity.toString()

            Glide.with(binding.imageProduct.context)
                .load(cart.productImage)
                .placeholder(R.drawable.ic_logo)
                .into(binding.imageProduct)

            binding.buttonDelete.setOnClickListener { onDeleteClick(cart.id) }
            binding.buttonBuy.setOnClickListener { onBuyClick(cart) }
            binding.buttonDecrease.setOnClickListener {
                if (cart.quantity > 1) {
                    onQuantityChange(cart.id, cart.quantity - 1)
                }
            }
            binding.buttonIncrease.setOnClickListener {
                onQuantityChange(cart.id, cart.quantity + 1)
            }
        }
    }
}