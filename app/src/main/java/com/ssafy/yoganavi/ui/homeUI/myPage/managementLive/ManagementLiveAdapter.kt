package com.ssafy.yoganavi.ui.homeUI.myPage.managementLive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.yoganavi.data.source.live.LiveLectureData
import com.ssafy.yoganavi.databinding.ListItemLiveBinding
import com.ssafy.yoganavi.ui.utils.formatDotDate
import com.ssafy.yoganavi.ui.utils.formatTime

class ManagementLiveAdapter(
    private val listener: (String) -> Unit,
) : ListAdapter<LiveLectureData, ManagementLiveAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(private val binding: ListItemLiveBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LiveLectureData) {
            val date = "${formatDotDate(item.startDate)}~${formatDotDate(item.endDate)}"
            binding.tvDate.text = date

            binding.tvLectureTitle.text = item.liveTitle

            val time = "${formatTime(item.startTime)}~${formatTime(item.endTime)}"
            binding.tvLectureTime.text = "${item.availableDay} | $time"
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ListItemLiveBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<LiveLectureData>() {
    override fun areItemsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem.liveId == newItem.liveId
    }

    override fun areContentsTheSame(oldItem: LiveLectureData, newItem: LiveLectureData): Boolean {
        return oldItem == newItem
    }
}