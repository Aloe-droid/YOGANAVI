package com.ssafy.yoganavi.ui.homeUI.myPage.courseHistory

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.yoganavi.databinding.FragmentCourseHistoryBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.COURSE_HISTORY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseHistoryFragment : BaseFragment<FragmentCourseHistoryBinding>(FragmentCourseHistoryBinding::inflate) {
    private val viewModel: CourseHistoryViewModel by viewModels()

    private val courseHistoryAdapter by lazy { CourseHistoryAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMyList.adapter = courseHistoryAdapter

        setToolbar(isBottomNavigationVisible = false,
            title = COURSE_HISTORY,
            canGoBack = true)

        initCollect()

        viewModel.getCourseHistoryList(::endSession)
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.courseHistoryList.collectLatest {
                courseHistoryAdapter.submitList(it)
            }
        }
    }
}
