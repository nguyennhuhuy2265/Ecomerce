package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.databinding.UserItemReviewBinding
import com.example.ecommerce.model.Review
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReviewAdapter : ListAdapter<Review, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = UserItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ReviewViewHolder(private val binding: UserItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: Review) {
            binding.tvUserName.text = review.userName ?: "Người dùng ẩn danh"
            binding.ratingBar.rating = review.rating.toFloat()
            binding.tvComment.text = review.comment ?: "Không có bình luận"
            binding.tvCreatedAt.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(review.createdAt?.toDate() ?: Date())
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<Review>() {
        override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean = oldItem == newItem
    }
}