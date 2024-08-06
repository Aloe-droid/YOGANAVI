package com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.data.source.dto.notice.NoticeData
import com.ssafy.yoganavi.databinding.ListItemNoticeBinding
import com.ssafy.yoganavi.ui.utils.formatDashWeekDate
import com.ssafy.yoganavi.ui.utils.loadImage
import com.ssafy.yoganavi.ui.utils.loadImageSequentially

class NoticeViewHolder(
    private val binding: ListItemNoticeBinding,
    private val navigateToRegisterNoticeFragment: (Int) -> Unit
) : ViewHolder(binding.root) {
    fun bind(item: NoticeData, noticeDeleteClick: (NoticeData) -> Unit) = with(binding) {
        if (item.profileImageSmallUrl.isNullOrBlank()) {
            binding.ivProfile.setImageResource(R.drawable.profilenull)
        } else {
            ivProfile.loadImage(item.profileImageSmallUrl)

            if (item.imageUrl?.isNotBlank() == true && item.imageUrlSmall?.isNotBlank() == true) {
                ivNotice.loadImageSequentially(item.imageUrlSmall, item.imageUrl)
            }
        }

        tvTeacherNickname.text = item.userName
        tvDate.text = formatDashWeekDate(item.updatedAt)
        tvContent.text = item.content

        binding.root.setOnClickListener {
            navigateToRegisterNoticeFragment(item.articleId)
        }
        tvDeleteBtn.setOnClickListener {
            noticeDeleteClick(item)
        }
    }
}