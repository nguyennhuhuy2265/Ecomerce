package com.example.ecommerce.adapter.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.databinding.ItemSellerOptionGroupBinding
import com.example.ecommerce.model.OptionGroup

class OptionGroupAdapter(
    private val optionGroups: MutableList<OptionGroup>,
    private val onEdit: (OptionGroup) -> Unit
) : RecyclerView.Adapter<OptionGroupAdapter.OptionGroupViewHolder>() {

    class OptionGroupViewHolder(
        private val binding: ItemSellerOptionGroupBinding,
        private val onEdit: (OptionGroup) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(optionGroup: OptionGroup) {
            binding.tvOptionGroupName.text = optionGroup.name
            binding.btnEdit.setOnClickListener { onEdit(optionGroup) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionGroupViewHolder {
        val binding = ItemSellerOptionGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OptionGroupViewHolder(binding, onEdit)
    }

    override fun onBindViewHolder(holder: OptionGroupViewHolder, position: Int) {
        holder.bind(optionGroups[position])
    }

    override fun getItemCount(): Int = optionGroups.size

    fun addOptionGroup(optionGroup: OptionGroup) {
        optionGroups.add(optionGroup)
        notifyDataSetChanged()
    }

    fun getOptionGroups(): List<OptionGroup> = optionGroups.toList()
}