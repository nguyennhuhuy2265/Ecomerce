package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.CartAdapter
import com.example.ecommerce.databinding.UserActivityCartBinding
import com.example.ecommerce.viewmodel.user.CartViewModel
import com.google.firebase.auth.FirebaseAuth

class CartActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityCartBinding
    private lateinit var viewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy userId
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Khởi tạo ViewModel
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)

        // Khởi tạo Adapter
        cartAdapter = CartAdapter(
            onDeleteClick = { cartId -> viewModel.removeItemFromCart(cartId, userId) },
            onQuantityChange = { cartId, newQuantity -> viewModel.updateQuantity(cartId, newQuantity, userId) },
            onBuyClick = { cart ->
                // Chuyển hướng đến CheckoutActivity
                val intent = Intent(this, CheckoutActivity::class.java).apply {
                    putExtra("cartItem", cart)
                }
                startActivity(intent)
                viewModel.removeItemFromCart(cartId = cart.id, userId = userId)
            }
        )

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        loadCart()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.recyclerViewCart.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCart.adapter = cartAdapter
    }

    private fun setupObservers() {
        viewModel.carts.observe(this) { carts ->
            carts?.let {
                cartAdapter.submitList(it)
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        // Loại bỏ observer cho orderActionResult vì không tạo đơn hàng ở đây nữa
    }

    private fun loadCart() {
        viewModel.loadCart(userId)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}