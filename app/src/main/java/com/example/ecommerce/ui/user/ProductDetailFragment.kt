package com.example.ecommerce.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.FragmentProductDetailUserBinding
import com.example.ecommerce.model.common.OptionGroup
import com.example.ecommerce.model.common.OptionValue
import com.example.ecommerce.model.common.Product

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailUserBinding? = null
    private val binding get() = _binding!!

    private lateinit var product: Product
    private val selectedOptions = mutableMapOf<String, OptionValue>() // Lưu lựa chọn của người dùng

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val receivedProduct = arguments?.getParcelable<Product>("product")
        if (receivedProduct != null) {
            product = receivedProduct
            displayProduct()
            setupOptionGroups()
            setupAddToCartButton()
        } else {
            Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    private fun displayProduct() {
        binding.tvProductName.text = product.name
        binding.tvPrice.text = "₫${product.price}"
        binding.tvDescription.text = product.description
        binding.tvShopLocation.text = product.shopLocation ?: "Không xác định"
        Glide.with(this)
            .load(product.defaultImageUrl ?: product.imageUrls.firstOrNull())
            .into(binding.ivProductImage)
    }

    private fun setupOptionGroups() {
        val llOptionGroups = binding.llOptionGroups
        llOptionGroups.removeAllViews()

        product.optionGroups.sortedBy { it.priority }.forEach { optionGroup ->
            // Tạo tiêu đề cho OptionGroup
            val tvGroupTitle = TextView(requireContext()).apply {
                text = optionGroup.name
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.black))
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 16, 0, 8)
                }
            }
            llOptionGroups.addView(tvGroupTitle)

            // Tạo RadioGroup để chứa các tùy chọn
            val radioGroup = RadioGroup(requireContext()).apply {
                orientation = RadioGroup.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Tạo RadioButton cho từng OptionValue
            optionGroup.values.forEachIndexed { index, optionValue ->
                val radioButton = RadioButton(requireContext()).apply {
                    text = if (optionValue.extraPrice > 0) {
                        "${optionValue.displayName} (+₫${optionValue.extraPrice})"
                    } else {
                        optionValue.displayName
                    }
                    id = index
                    tag = optionValue
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                    isChecked = optionValue.isDefault
                }
                radioGroup.addView(radioButton)

                // Nếu là mặc định, chọn sẵn giá trị
                if (optionValue.isDefault) {
                    selectedOptions[optionGroup.id] = optionValue
                }
            }

            // Xử lý khi người dùng chọn một tùy chọn
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val selectedRadioButton = radioGroup.findViewById<RadioButton>(checkedId)
                val selectedValue = selectedRadioButton.tag as OptionValue
                selectedOptions[optionGroup.id] = selectedValue
            }

            llOptionGroups.addView(radioGroup)
        }
    }

    private fun setupAddToCartButton() {
        binding.btnAddToCart.setOnClickListener {
            // Kiểm tra xem tất cả OptionGroup bắt buộc đã được chọn chưa
            val missingRequiredGroups = product.optionGroups.filter { group ->
                group.isRequired && !selectedOptions.containsKey(group.id)
            }

            if (missingRequiredGroups.isNotEmpty()) {
                val missingGroupsText = missingRequiredGroups.joinToString(", ") { it.name }
                Toast.makeText(
                    requireContext(),
                    "Vui lòng chọn: $missingGroupsText",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Tính tổng giá sau khi chọn các tùy chọn
            val basePrice = product.price
            val extraPrice = selectedOptions.values.sumOf { it.extraPrice }
            val totalPrice = basePrice + extraPrice

            // Lưu vào giỏ hàng (giả định, bạn có thể thay bằng logic thực tế)
            Toast.makeText(
                requireContext(),
                "Đã thêm vào giỏ hàng: ${product.name} - Tổng: ₫$totalPrice",
                Toast.LENGTH_SHORT
            ).show()

            // Quay lại màn trước
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}