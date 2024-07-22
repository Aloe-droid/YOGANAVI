package com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.ssafy.yoganavi.data.source.lecture.LectureDetailData
import com.ssafy.yoganavi.data.source.lecture.VideoChapterData
import com.ssafy.yoganavi.databinding.FragmentRegisterVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.registerVideo.chapter.ChapterAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterVideoFragment : BaseFragment<FragmentRegisterVideoBinding>(
    FragmentRegisterVideoBinding::inflate
) {
    private val args by navArgs<RegisterVideoFragmentArgs>()
    private val viewModel: RegisterVideoViewModel by viewModels()
    private val chapterAdapter by lazy { ChapterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.recordedId != -1) viewModel.getChapters(args.recordedId)

        binding.rvLecture.adapter = chapterAdapter
        initCollect()
        initListener()
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureState.collectLatest {
                setView(it)
            }
        }
    }

    private fun initListener() = with(binding) {
        btnAddChapter.setOnClickListener {
            val list = chapterAdapter.currentList.toMutableList()
            val number = list.lastOrNull()?.chapterNumber ?: -1
            list.add(VideoChapterData(chapterNumber = number + 1))
            chapterAdapter.submitList(list.toMutableList())
        }
    }

    private fun setView(data: LectureDetailData) = with(binding) {
        etContent.setText(data.recordTitle)
        etContent.setText(data.recordContent)
        chapterAdapter.submitList(data.recordedLectureChapters.toMutableList())
    }
}