package com.example.ecommerce.adapter.seller

import com.example.ecommerce.R

class ProductImageAdapter(
    private val imageUrls: MutableList<String>,
    private val onRemoveClick: (Int) -> Unit
) : androidx.recyclerview.widget.RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: android.view.View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val imageView: android.widget.ImageView = itemView.findViewById(R.id.iv_product_image)
        val removeButton: android.widget.ImageButton = itemView.findViewById(R.id.btn_remove_image)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ImageViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.seller_item_product_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        com.bumptech.glide.Glide.with(holder.imageView.context)
            .load(imageUrl)
            .into(holder.imageView)
        holder.removeButton.setOnClickListener {
            onRemoveClick(position)
        }
    }

    override fun getItemCount(): Int = imageUrls.size
}