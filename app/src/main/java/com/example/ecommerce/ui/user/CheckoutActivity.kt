package com.example.ecommerce.ui.user

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.CheckoutItemAdapter
import com.example.ecommerce.databinding.UserActivityCheckoutBinding
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Card
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
    private var isUsingSavedCard: Boolean = true // Biến để theo dõi nếu dùng thẻ đã lưu
    private var isAddressFormOpen: Boolean = false // Biến để theo dõi trạng thái form địa chỉ

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

        // Mặc định ẩn form thẻ vì rbCashOnDelivery được chọn
        binding.layoutCardInfo.visibility = View.GONE
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Ẩn tiêu đề mặc định của ActionBar
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        binding.rvCheckoutItems.layoutManager = LinearLayoutManager(this)
        binding.rvCheckoutItems.adapter = checkoutItemAdapter
    }

    private fun setupObservers() {
        viewModel.user.observe(this) { user ->
            user?.let {
                // Hiển thị địa chỉ giao hàng
                if (it.address != null && !isAddressFormOpen) {
                    binding.layoutExistingAddress.visibility = View.VISIBLE
                    binding.layoutAddressForm.visibility = View.GONE
                    binding.tvRecipientName.text = it.name
                    val phone = it.address?.phoneNumber ?: it.phoneNumber
                    binding.tvPhoneNumber.text = phone?.let { "$it" } ?: "Chưa có số điện thoại"
                    binding.tvAddress.text = "${it.address.streetNumber} ${it.address.streetName}, ${it.address.ward}, ${it.address.district}, ${it.address.city}"
                } else {
                    binding.layoutExistingAddress.visibility = View.GONE
                    binding.layoutAddressForm.visibility = View.VISIBLE
                }

                // Hiển thị thông tin thẻ nếu có
                if (it.card != null && binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbCreditCard) {
                    binding.layoutSavedCard.visibility = View.VISIBLE
                    binding.layoutCardForm.visibility = View.GONE
                    isUsingSavedCard = true
                    binding.tvBankName.text = it.card.bankName
                    binding.tvCardNumber.text = "**** **** **** ${it.card.cardNumber.takeLast(4)}"
                    binding.tvCardHolder.text = it.card.cardHolderName
                } else if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbCreditCard) {
                    binding.layoutSavedCard.visibility = View.GONE
                    binding.layoutCardForm.visibility = View.VISIBLE
                    isUsingSavedCard = false
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
        // Xử lý thay đổi địa chỉ
        binding.tvChangeAddress.setOnClickListener {
            if (!isAddressFormOpen && viewModel.user.value?.address != null) {
                // Mở form nhập địa chỉ
                binding.layoutExistingAddress.visibility = View.GONE
                binding.layoutAddressForm.visibility = View.VISIBLE
                isAddressFormOpen = true
                binding.tvChangeAddress.text = "Hủy thay đổi"
            } else {
                // Đóng form và hiển thị địa chỉ cũ
                binding.layoutExistingAddress.visibility = View.VISIBLE
                binding.layoutAddressForm.visibility = View.GONE
                isAddressFormOpen = false
                binding.tvChangeAddress.text = "Thay đổi địa chỉ"
            }
        }

        // Xử lý thay đổi thẻ
        binding.tvChangeCard.setOnClickListener {
            binding.layoutSavedCard.visibility = View.GONE
            binding.layoutCardForm.visibility = View.VISIBLE
            isUsingSavedCard = false
        }

        // Xử lý chọn phương thức thanh toán
        binding.rgPaymentMethods.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbCreditCard) {
                binding.layoutCardInfo.visibility = View.VISIBLE
                viewModel.user.value?.let { user ->
                    if (user.card != null) {
                        binding.layoutSavedCard.visibility = View.VISIBLE
                        binding.layoutCardForm.visibility = View.GONE
                        isUsingSavedCard = true
                        binding.tvBankName.text = user.card.bankName
                        binding.tvCardNumber.text = "**** **** **** ${user.card.cardNumber.takeLast(4)}"
                        binding.tvCardHolder.text = user.card.cardHolderName
                    } else {
                        binding.layoutSavedCard.visibility = View.GONE
                        binding.layoutCardForm.visibility = View.VISIBLE
                        isUsingSavedCard = false
                    }
                }
            } else {
                binding.layoutCardInfo.visibility = View.GONE
            }
        }

        // Xử lý đặt hàng
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

            if (address == null) {
                Toast.makeText(this, "Vui lòng điền đầy đủ địa chỉ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Xử lý thông tin thẻ nếu chọn thanh toán bằng thẻ
            if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbCreditCard && !isUsingSavedCard) {
                val card = Card(
                    bankName = binding.etBankName.text.toString(),
                    cardNumber = binding.etCardNumber.text.toString(),
                    cardHolderName = binding.etCardHolderName.text.toString(),
                    expiryDate = binding.etExpiryDate.text.toString(),
                    cvv = binding.etCVV.text.toString()
                ).takeIf {
                    it.bankName.isNotEmpty() &&
                            it.cardNumber.isNotEmpty() &&
                            it.cardHolderName.isNotEmpty() &&
                            it.expiryDate.isNotEmpty() &&
                            it.cvv.isNotEmpty()
                }

                if (card == null) {
                    Toast.makeText(this, "Vui lòng điền đầy đủ thông tin thẻ", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Cập nhật thẻ vào user trước khi đặt hàng
                viewModel.user.value?.let { user ->
                    val updatedUser = user.copy(card = card)
                    viewModel.updateUser(updatedUser)
                }
            }

            // Nếu chọn thanh toán bằng thẻ, hiển thị dialog xác nhận
            if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbCreditCard) {
                showPaymentConfirmationDialog(paymentStatus, address)
            } else {
                viewModel.placeOrder(userId, paymentStatus, address)
            }
        }
    }

    private fun showPaymentConfirmationDialog(paymentStatus: PaymentStatus, address: Address) {
        val cardNumber = binding.tvCardNumber.text ?: binding.etCardNumber.text
        val message = "Bạn có chắc chắn muốn sử dụng thẻ $cardNumber để thanh toán số tiền ${binding.tvTotalAmount.text}?"

        val dialog = AlertDialog.Builder(this)
            .setTitle("Xác nhận thanh toán")
            .setMessage(message)
            .setPositiveButton("Xác nhận") { _, _ ->
                viewModel.placeOrder(userId, paymentStatus, address)
            }
            .setNegativeButton("Hủy") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()

        dialog.setOnShowListener {
            // Bo góc dialog
            dialog.window?.setBackgroundDrawableResource(R.drawable.bg_filter_normal)

            // Đổi màu nút (ví dụ: xanh dương cho xác nhận, xám cho hủy)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.primary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.gray_dark))
        }

        dialog.show()
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