package com.sportmaster.surelykmp.activities.comment.presentation.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportmaster.surelykmp.activities.freecodes.data.model.Code
import com.sportmaster.surelykmp.activities.freecodes.data.model.Comment
import com.sportmaster.surelykmp.core.data.remote.CodesApiService
import com.sportmaster.surelykmp.core.data.remote.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommentUiState(
    val code: Code? = null,
    val isLoading: Boolean = false,
    val isCommentPosting: Boolean = false,
    val isRating: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val selectedRating: Double = 0.0,
    val commentText: String = ""
)

class CommentViewModel(
    private val apiService: CodesApiService,
    private val initialCode: Code
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommentUiState(code = initialCode))
    val uiState: StateFlow<CommentUiState> = _uiState.asStateFlow()

    fun onCommentTextChanged(text: String) {
        _uiState.update { it.copy(commentText = text) }
    }

    fun onRatingSelected(rating: Double) {
        _uiState.update { it.copy(selectedRating = rating) }
    }

    fun postComment(currentUser: String, currentUserAvatar: String?) {
        val commentText = _uiState.value.commentText
        val codeId = _uiState.value.code?._id ?: return

        if (commentText.isBlank()) {
            _uiState.update { it.copy(error = "Comment cannot be empty") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCommentPosting = true, error = null) }

            when (val result = apiService.addComment(codeId, commentText)) {
                is Result.Success -> {
                    // Create optimistic comment with current time
                    val newComment = Comment(
                        user = currentUser,
                        comment = commentText,
                        createdAt = "just now",
                        image = currentUserAvatar
                    )

                    // Add comment to the list
                    _uiState.update { state ->
                        val updatedComments = state.code?.comments.orEmpty().toMutableList()
                        updatedComments.add(0, newComment)

                        state.copy(
                            code = state.code?.copy(comments = updatedComments),
                            commentText = "",
                            isCommentPosting = false,
                            successMessage = "Comment posted successfully"
                        )
                    }

                    // Clear success message after a delay
                    kotlinx.coroutines.delay(2000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isCommentPosting = false,
                            error = "Failed to post comment. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun rateCode() {
        val rating = _uiState.value.selectedRating
        val codeId = _uiState.value.code?._id ?: return

        if (rating <= 0) {
            _uiState.update { it.copy(error = "Please select a rating") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isRating = true, error = null) }

            when (val result = apiService.addRating(codeId, rating)) {
                is Result.Success -> {
                    // Calculate new average rating
                    val currentRating = _uiState.value.code?.rating ?: 0.0
                    val newRating = (rating + currentRating) / 2.0

                    _uiState.update { state ->
                        state.copy(
                            code = state.code?.copy(rating = newRating),
                            isRating = false,
                            selectedRating = 0.0,
                            successMessage = "Rating submitted successfully"
                        )
                    }

                    // Clear success message after a delay
                    kotlinx.coroutines.delay(2000)
                    _uiState.update { it.copy(successMessage = null) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isRating = false,
                            error = "Failed to submit rating. Please try again."
                        )
                    }
                }
            }
        }
    }

    fun refreshCode() {
        val codeId = _uiState.value.code?._id ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            when (val result = apiService.getCodeById(codeId)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            code = result.data,
                            isLoading = false
                        )
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to refresh code"
                        )
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }
}