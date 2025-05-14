package com.example.ecommerce.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.databinding.UserActivityOrderBinding
import com.example.ecommerce.ui.user.OrderFragment

class OrderActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Thêm OrderFragment vào Activity
        val fragment = OrderFragment().apply {
            arguments = Bundle().apply {
                putString("status", intent.getStringExtra("status"))
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }
}