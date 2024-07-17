package com.ssafy.yoganavi.ui.loginUI.join

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.ssafy.yoganavi.data.source.YogaResponse
import com.ssafy.yoganavi.databinding.FragmentJoinBinding
import com.ssafy.yoganavi.ui.core.BaseFragment
import com.ssafy.yoganavi.ui.utils.PASSWORD_DIFF
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class JoinFragment : BaseFragment<FragmentJoinBinding>(FragmentJoinBinding::inflate) {
    private val viewModel: JoinViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPasswordWatcher()
        initCollect()
        initListener()
    }

    private fun initListener() {
        binding.tvTeacher.setOnClickListener {
            binding.tvTeacher.toggle()
        }

        binding.btnSend.setOnClickListener {
            hideKeyboard()

            val email = binding.tieId.text.toString()
            viewModel.registerEmail(email)
        }

        binding.btnCheck.setOnClickListener {
            hideKeyboard()

            val number = binding.tieCn.text.toString().toIntOrNull()
            viewModel.checkAuthEmail(number)
        }

        binding.btnSignup.setOnClickListener {
            val email = binding.tieId.text.toString()
            val password = binding.tiePw.text.toString()
            val passwordAgain = binding.tiePwAgain.text.toString()
            val nickname = binding.tieNn.text.toString()
            val isTeacher = binding.tvTeacher.isChecked
            viewModel.signUp(email, password, passwordAgain, nickname, isTeacher)
        }
    }

    private fun initCollect() = viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.joinEvent.collectLatest {
                when (it) {
                    is JoinEvent.RegisterEmailSuccess -> registerEmailSuccess(it.data)
                    is JoinEvent.CheckEmailSuccess -> checkEmailSuccess(it.data)
                    is JoinEvent.SignUpSuccess -> signUpSuccess(it.data)
                    is JoinEvent.Error -> error(it.message)
                }
            }
        }
    }

    private fun registerEmailSuccess(data: YogaResponse<Unit>?) = data?.let {
        binding.btnCheck.isEnabled = true
        binding.btnSignup.isEnabled = false
        showSnackBar(it.message)
    }

    private fun checkEmailSuccess(data: YogaResponse<Unit>?) = data?.let {
        binding.btnCheck.isEnabled = false
        binding.btnSignup.isEnabled = true
        showSnackBar(it.message)
    }

    private fun signUpSuccess(data: YogaResponse<Unit>?) = data?.let {
        showSnackBar(it.message)
    }

    private fun error(message: String?) = message?.let {
        showSnackBar(it)
    }

    private fun initPasswordWatcher() = binding.tiePwAgain.addTextChangedListener {
        if (it.toString() != binding.tiePw.text.toString()) {
            binding.tiePwAgain.error = PASSWORD_DIFF
        } else {
            binding.tiePwAgain.error = null
        }
    }

    private fun hideKeyboard() {
        if (activity != null && requireActivity().currentFocus != null) {
            val inputManager: InputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
}
