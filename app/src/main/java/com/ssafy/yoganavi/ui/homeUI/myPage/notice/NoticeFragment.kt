package com.ssafy.yoganavi.ui.homeUI.myPage.notice

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.yoganavi.R
import com.ssafy.yoganavi.databinding.FragmentNoticeBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.homeUI.myPage.managementVideo.ManagementVideoFragmentDirections
import com.ssafy.yoganavi.ui.homeUI.myPage.notice.notices.NoticeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoticeFragment : BaseFragment<FragmentNoticeBinding>(FragmentNoticeBinding::inflate) {

    private val viewModel: NoticeViewModel by viewModels()
    private val noticeAdapter by lazy { NoticeAdapter(::navigateToNoticeFragment) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMyList.adapter = noticeAdapter
        initCollect()
        initListener()
        viewModel.getNoticeAll()
    }

    private fun initListener() {
        with(binding) {
            floatingActionButton.setOnClickListener {
                findNavController().navigate(R.id.action_noticeFragment_to_registerNoticeFragment)
            }
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.noticeList.collectLatest {
                noticeAdapter.submitList(it)
            }
        }
    }

    private fun navigateToNoticeFragment(recordedId: Int = -1) {
        val directions = ManagementVideoFragmentDirections
            .actionManagementVideoFragmentToRegisterVideoFragment(recordedId)

        findNavController().navigate(directions)
    }
}