package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.databinding.UaseItemOptionGroupBinding
import com.example.ecommerce.model.OptionGroup

class OptionGroupAdapter(
    private val onOptionSelected: (groupName: String, option: String) -> Unit
) : RecyclerView.Adapter<OptionGroupAdapter.OptionGroupViewHolder>() {

    private var optionGroups = emptyList<OptionGroup>()

    fun submitList(groups: List<OptionGroup>) {
        optionGroups = groups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionGroupViewHolder {
        val binding = UaseItemOptionGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OptionGroupViewHolder, position: Int) {
        holder.bind(optionGroups[position])
    }

    override fun getItemCount(): Int = optionGroups.size

    inner class OptionGroupViewHolder(private val binding: UaseItemOptionGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: OptionGroup) {
            binding.tvGroupName.text = group.name
            binding.rgOptions.removeAllViews() // Xóa các lựa chọn cũ

            group.values.forEach { option ->
                val radioButton = android.widget.RadioButton(binding.rgOptions.context).apply {
                    text = option
                    setOnClickListener { onOptionSelected(group.name, option) }
                }
                binding.rgOptions.addView(radioButton)
            }
        }
    }
}