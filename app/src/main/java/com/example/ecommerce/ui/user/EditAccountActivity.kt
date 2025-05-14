package com.example.ecommerce.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.databinding.UserActivityEditAccountBinding
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Card
import com.example.ecommerce.model.User
import com.example.ecommerce.viewmodel.user.AccountViewModel

class EditAccountActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityEditAccountBinding
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityEditAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(AccountViewModel::class.java)
        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                binding.etName.setText(it.name)
                binding.etEmail.setText(it.email)
                binding.etPhone.setText(it.phoneNumber ?: "")
                it.address?.let { address ->
                    binding.etStreetNumber.setText(address.streetNumber ?: "")
                    binding.etStreetName.setText(address.streetName ?: "")
                    binding.etWard.setText(address.ward ?: "")
                    binding.etDistrict.setText(address.district ?: "")
                    binding.etCity.setText(address.city ?: "")
                }
                it.card?.let { card ->
                    binding.etBankName.setText(card.bankName)
                    binding.etCardNumber.setText(card.cardNumber)
                    binding.etCardHolderName.setText(card.cardHolderName)
                    binding.etExpiryDate.setText(card.expiryDate)
                    binding.etCVV.setText(card.cvv)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnUpdate.setOnClickListener {
            val updatedUser = viewModel.user.value?.copy(
                name = binding.etName.text.toString(),
                phoneNumber = binding.etPhone.text.toString(),
                address = Address(
                    streetNumber = binding.etStreetNumber.text.toString(),
                    streetName = binding.etStreetName.text.toString(),
                    ward = binding.etWard.text.toString(),
                    district = binding.etDistrict.text.toString(),
                    city = binding.etCity.text.toString()
                ),
                card = Card(
                    bankName = binding.etBankName.text.toString(),
                    cardNumber = binding.etCardNumber.text.toString(),
                    cardHolderName = binding.etCardHolderName.text.toString(),
                    expiryDate = binding.etExpiryDate.text.toString(),
                    cvv = binding.etCVV.text.toString()
                )
            )
            updatedUser?.let {
                viewModel.updateUser(it) // Gọi updateUser từ ViewModel
                finish()
            }
        }
    }
}