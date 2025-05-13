package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.CheckoutItemAdapter
import com.example.ecommerce.databinding.UserActivityCheckoutBinding
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Cart
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.user.CheckoutViewModel
import com.google.firebase.auth.FirebaseAuth

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityCheckoutBinding
    private lateinit var viewModel: CheckoutViewModel
    private lateinit var checkoutItemAdapter: CheckoutItemAdapter
    private lateinit var userId: String
    private var selectedQuantity: Int = 1
    private var selectedOptions: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this).get(CheckoutViewModel::class.java)
        checkoutItemAdapter = CheckoutItemAdapter()

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupListeners()
        loadData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutItems.adapter = checkoutItemAdapter
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                if (it.address != null) {
                    binding.layoutExistingAddress.visibility = View.VISIBLE
                    binding.layoutAddressForm.visibility = View.GONE
                    binding.tvRecipientName.text = it.name
                    binding.tvPhoneNumber.text = it.address.phoneNumber ?: "Chưa có số điện thoại"
                    binding.tvAddress.text = "${it.address.streetNumber} ${it.address.streetName}, ${it.address.ward}, ${it.address.district}, ${it.address.city}"
                } else {
                    binding.layoutExistingAddress.visibility = View.GONE
                    binding.layoutAddressForm.visibility = View.VISIBLE
                }
            }
        }

        viewModel.checkoutItems.observe(this) { items ->
            checkoutItemAdapter.submitList(items)
            val subtotal = items.sumOf { it.unitPrice * it.quantity }
            val shippingFee = 30000.0
            val total = subtotal + shippingFee
            binding.tvSubtotal.text = "₫${subtotal.toInt()}"
            binding.tvShippingFee.text = "₫${shippingFee.toInt()}"
            binding.tvTotalAmount.text = "₫${total.toInt()}"
            binding.tvBottomTotalAmount.text = "₫${total.toInt()}"
        }

        viewModel.orderActionResult.observe(this) { result ->
            result?.let {
                Toast.makeText(this, "Đặt hàng thành công! Mã đơn: $it", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupListeners() {
        binding.btnPlaceOrder.setOnClickListener {
            val paymentStatus = if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbCashOnDelivery) {
                PaymentStatus.PENDING
            } else {
                PaymentStatus.PAID
            }

            val address = if (binding.layoutExistingAddress.visibility == View.VISIBLE) {
                viewModel.user.value?.address
            } else {
                Address(
                    phoneNumber = binding.etPhoneNumber.text.toString(),
                    streetNumber = binding.etStreetNumber.text.toString(),
                    streetName = binding.etStreetName.text.toString(),
                    ward = binding.etWard.text.toString(),
                    district = binding.etDistrict.text.toString(),
                    city = binding.etCity.text.toString(),
                    country = "Vietnam"
                ).takeIf {
                    it.phoneNumber?.isNotEmpty() == true &&
                            it.streetNumber?.isNotEmpty() == true &&
                            it.streetName?.isNotEmpty() == true &&
                            it.ward?.isNotEmpty() == true &&
                            it.district?.isNotEmpty() == true &&
                            it.city?.isNotEmpty() == true
                }
            }

            if (address != null) {
                viewModel.placeOrder(userId, paymentStatus, address)
            } else {
                Toast.makeText(this, "Vui lòng điền đầy đủ địa chỉ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadData() {
        viewModel.loadUser(userId)

        val cartItem = intent.getParcelableExtra<Cart>("cartItem")
        val product = intent.getParcelableExtra<Product>("product")

        if (cartItem != null) {
            viewModel.loadCheckoutItemsFromCart(cartItem)
        } else if (product != null) {
            selectedQuantity = intent.getIntExtra("quantity", 1)
            selectedOptions = intent.getStringArrayListExtra("selectedOptions") ?: emptyList()
            viewModel.loadCheckoutItemsFromProduct(product, selectedQuantity, selectedOptions)
        } else {
            Toast.makeText(this, "Không tìm thấy sản phẩm để thanh toán", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}