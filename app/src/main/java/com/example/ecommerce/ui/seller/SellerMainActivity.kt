package com.example.ecommerce.ui.seller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.databinding.ActivitySellerMainBinding

class SellerMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySellerMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySellerMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}