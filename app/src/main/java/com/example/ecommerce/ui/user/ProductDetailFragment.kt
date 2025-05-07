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
import com.example.ecommerce.databinding.FragmentProductDetailUserBinding
import com.example.ecommerce.model.common.OptionGroup
import com.example.ecommerce.model.common.OptionValue
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.repository.CloudinaryRepository

class ProductDetailFragment : Fragment() {
    private var _binding: FragmentProductDetailUserBinding? = null
    private val binding get() = _binding!!
    private lateinit var product: Product
    private val selectedOptions = mutableMapOf<String, OptionValue>()
    private val cloudinaryRepository = CloudinaryRepository(requireContext())

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
        val imageUrl = product.default_image_public_id?.let {
            cloudinaryRepository.getImageUrl(it, 400, 400, "fill")
        } ?: (product.image_public_ids.firstOrNull()?.let {
            cloudinaryRepository.getImageUrl(it, 400, 400, "fill")
        } ?: "")
        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .into(binding.ivProductImage)
    }

    private fun setupOptionGroups() {
        val llOptionGroups = binding.llOptionGroups
        llOptionGroups.removeAllViews()

        product.optionGroups.sortedBy { it.priority }.forEach { optionGroup ->
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

            val radioGroup = RadioGroup(requireContext()).apply {
                orientation = RadioGroup.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

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

                if (optionValue.isDefault) {
                    selectedOptions[optionGroup.id] = optionValue
                }
            }

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

            val basePrice = product.price
            val extraPrice = selectedOptions.values.sumOf { it.extraPrice }
            val totalPrice = basePrice + extraPrice

            Toast.makeText(
                requireContext(),
                "Đã thêm vào giỏ hàng: ${product.name} - Tổng: ₫$totalPrice",
                Toast.LENGTH_SHORT
            ).show()

            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}