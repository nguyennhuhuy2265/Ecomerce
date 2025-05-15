package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Review
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val reviewRepo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _submitResult = MutableLiveData<Result<Unit>>()
    val submitResult: LiveData<Result<Unit>> get() = _submitResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun submitReview(reviewId: String, productId: String, rating: Int, comment: String?, imageUrls: List<String>) {
        viewModelScope.launch {
            if (rating == 0) {
                _error.value = "Vui lòng chọn số sao đánh giá"
                return@launch
            }

            userRepo.getCurrentUserInfo().onSuccess { user ->
                reviewRepo.getReviewsByProductId(productId).onSuccess { reviews ->
                    if (reviews.any { it.userId == user?.id }) {
                        _error.value = "Bạn đã đánh giá sản phẩm này rồi"
                        return@onSuccess
                    }

                    val review = Review(
                        id = reviewId, // Sử dụng reviewId từ tham số
                        productId = productId,
                        userId = user?.id ?: "",
                        userName = user?.name,
                        userAvatarUrl = user?.avatarUrl,
                        rating = rating,
                        comment = comment?.takeIf { it.isNotBlank() },
                        imageUrls = imageUrls,
                        createdAt = Timestamp.now()
                    )

                    reviewRepo.addReview(review).onSuccess {
                        _submitResult.value = Result.success(Unit)
                    }.onFailure { error ->
                        _error.value = "Lỗi khi lưu đánh giá: ${error.message}"
                    }
                }.onFailure { error ->
                    _error.value = "Lỗi khi kiểm tra đánh giá: ${error.message}"
                }
            }.onFailure { error ->
                _error.value = "Lỗi khi lấy thông tin người dùng: ${error.message}"
            }
        }
    }
}