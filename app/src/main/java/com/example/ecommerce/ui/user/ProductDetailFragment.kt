package com.example.ecommerce.ui.user

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
import com.example.ecommerce.databinding.UserFragmentProductDetailBinding
import com.example.ecommerce.model.OptionGroup
import com.example.ecommerce.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {
    private var _binding: UserFragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var product: Product
    private val selectedOptions = mutableMapOf<String, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = UserFragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getParcelable<Product>("product")?.let { product = it } ?: run {
            Toast.makeText(requireContext(), "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        // Hiển thị thông tin sản phẩm
        with(binding) {
            tvProductName.text = product.name
            val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
            tvPrice.text = "₫${formatter.format(product.price)}"
            tvDescription.text = product.description
            tvShopLocation.text = product.shopLocation ?: "Không xác định"
            Glide.with(this@ProductDetailFragment)
                .load(product.imageUrls.firstOrNull() ?: "")
                .into(ivProductImage)
        }

        // Thiết lập các tùy chọn
        binding.llOptionGroups.apply {
            removeAllViews()
            product.optionGroups.forEach { group ->
                addView(TextView(requireContext()).apply {
                    text = group.name
                    textSize = 16f
                    setTextColor(resources.getColor(android.R.color.black))
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 16, 0, 8) }
                })

                val radioGroup = RadioGroup(requireContext()).apply {
                    orientation = RadioGroup.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                }

                group.values.forEachIndexed { index, value ->
                    radioGroup.addView(RadioButton(requireContext()).apply {
                        text = value
                        id = index
                        tag = value
                        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    })
                }

                radioGroup.setOnCheckedChangeListener { _, checkedId ->
                    selectedOptions[group.id] = radioGroup.findViewById<RadioButton>(checkedId).tag as String
                }
                addView(radioGroup)
            }
        }

        // Thêm vào giỏ hàng
        binding.btnAddToCart.setOnClickListener {
            val totalPrice = product.price
            Toast.makeText(requireContext(), "Đã thêm vào giỏ hàng: ${product.name} - Tổng: ₫${NumberFormat.getNumberInstance(Locale("vi", "VN")).format(totalPrice)}", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}