package com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.databinding.FragmentManagementVideoBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.core.MainEvent
import com.ssafy.yoganavi.ui.core.MainViewModel
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.lecture.LectureAdapter
import com.ssafy.yoganavi.ui.utils.MANAGEMENT_VIDEO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ManagementVideoFragment : BaseFragment<FragmentManagementVideoBinding>(
    FragmentManagementVideoBinding::inflate
) {
    private val viewModel: ManagementVideoViewModel by viewModels()
    private val activityViewModel: MainViewModel by activityViewModels()
    private val lectureAdapter by lazy { LectureAdapter(::navigateToRegisterVideoFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()

        binding.rvLecture.adapter = lectureAdapter
        initCollect()
        initListener()
        viewModel.getLectureList()
    }

    private fun setToolbar() {
        val mainEvent = MainEvent(
            isBottomNavigationVisible = false,
            title = MANAGEMENT_VIDEO,
            canGoBack = true,
        )
        activityViewModel.setMainEvent(mainEvent)
    }

    private fun initListener() {
        binding.fabEdit.setOnClickListener {
            navigateToRegisterVideoFragment()
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.lectureList.collectLatest {
                lectureAdapter.submitList(it)
            }
        }
    }

    private fun navigateToRegisterVideoFragment(recordedId: Int = -1) {
        val directions = ManagementVideoFragmentDirections
            .actionManagementVideoFragmentToRegisterVideoFragment(recordedId)

        findNavController().navigate(directions)
    }
}
