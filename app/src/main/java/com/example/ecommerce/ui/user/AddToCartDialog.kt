package com.example.ecommerce.ui.user

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.OptionGroupAdapter
import com.example.ecommerce.databinding.UserDialogAddToCartBinding
import com.example.ecommerce.model.Product

class AddToCartDialog(
    context: Context,
    private val product: Product,
    private val onAddToCart: (selectedOptions: List<String>, quantity: Int) -> Unit
) : Dialog(context) {

    private lateinit var binding: UserDialogAddToCartBinding
    private var quantity = 1
    private val unitPrice: Double = product.price.toDouble()
    private val selectedOptions = mutableMapOf<String, String>()
    private lateinit var optionGroupAdapter: OptionGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserDialogAddToCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setCancelable(true)

        setupViews()
        setupRecyclerView()
        setupListeners()
    }

    private fun setupViews() {
        binding.tvProductName.text = product.name
        updatePrice()
        binding.tvQuantity.text = quantity.toString()
    }

    private fun setupRecyclerView() {
        optionGroupAdapter = OptionGroupAdapter { groupName, option ->
            selectedOptions[groupName] = option
        }
        binding.rvOptionGroups.layoutManager = LinearLayoutManager(context)
        binding.rvOptionGroups.adapter = optionGroupAdapter
        optionGroupAdapter.submitList(product.optionGroups)
    }

    private fun updatePrice() {
        val totalPrice = unitPrice * quantity
        binding.tvPrice.text = "Giá: ₫$totalPrice"
    }

    private fun setupListeners() {
        binding.btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
                updatePrice()
            }
        }

        binding.btnIncrease.setOnClickListener {
            quantity++
            binding.tvQuantity.text = quantity.toString()
            updatePrice()
        }

        binding.btnAddToCart.setOnClickListener {
            if (product.optionGroups.isNotEmpty()) {
                product.optionGroups.forEach { group ->
                    if (!selectedOptions.containsKey(group.name)) {
                        Toast.makeText(context, "Vui lòng chọn ${group.name}", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            val selectedOptionsList = product.optionGroups.mapNotNull { selectedOptions[it.name] }
            onAddToCart(selectedOptionsList, quantity)
            dismiss()
        }
    }
}