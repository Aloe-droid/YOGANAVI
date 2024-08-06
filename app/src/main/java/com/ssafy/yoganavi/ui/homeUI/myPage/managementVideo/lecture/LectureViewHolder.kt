package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnailBinding
import com.ssafy.yoganavi.ui.utils.loadImage
import com.ssafy.yoganavi.ui.utils.toK

class LectureViewHolder(
    private val binding: ListItemLectureThumbnailBinding,
    private val navigateToLectureDetailFragment: ((Long, String) -> Unit)? = null,
    private val navigateToRegisterVideoFragment: ((Long) -> Unit)? = null,
    private val sendLikeLecture: (Long) -> Unit
) : ViewHolder(binding.root) {

    private var likeCount = 0

    fun bind(data: LectureData) = with(binding) {
        tvTitle.text = data.recordTitle
        tvCount.text = data.likeCount.toK()
        ivFavorite.isSelected = data.myLike
        likeCount = data.likeCount

        ivThumbnail.loadImage(data.recordThumbnailSmall)

        setListener(data)
    }

    private fun setListener(data: LectureData) = with(binding) {
        ivFavorite.setOnClickListener {
            ivFavorite.isSelected = !ivFavorite.isSelected
            if (ivFavorite.isSelected) likeCount++ else likeCount--
            tvCount.text = likeCount.toK()

            sendLikeLecture(data.recordedId)
        }

        ivThumbnail.setOnClickListener {
            navigateToRegisterVideoFragment?.invoke(data.recordedId)
            navigateToLectureDetailFragment?.invoke(data.recordedId, data.nickname)
        }
    }
}