package com.ssafy.yoganavi.ui.homeUI.teacher.teacherDetail.teacherDetail.lecture


import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.data.source.dto.lecture.LectureData
import com.ssafy.yoganavi.databinding.ListItemLectureThumbnail2Binding
import com.ssafy.yoganavi.ui.utils.loadImage
import com.ssafy.yoganavi.ui.utils.toK

class TeacherDetailLectureViewHolder(
    private val binding: ListItemLectureThumbnail2Binding,
    private val navigateToLectureDetailFragment: (Long) -> Unit,
    private val sendLikeLecture: (Long) -> Unit
) : ViewHolder(binding.root) {
    fun bind(data: LectureData) = with(binding) {
        var cnt = data.likeCount
        var isSelected = data.myLike
        tvTitle.text = data.recordTitle
        tvCount.text = cnt.toK()
        ivFavorite.isSelected = isSelected

        ivThumbnail.loadImage(data.recordThumbnailSmall)

        root.setOnClickListener { navigateToLectureDetailFragment(data.recordedId) }

        ivFavorite.setOnClickListener {
            if (isSelected) cnt -= 1
            else cnt += 1
            tvCount.text = cnt.toK()
            isSelected = !isSelected
            ivFavorite.isSelected = isSelected
            sendLikeLecture(data.recordedId)
        }
    }
}