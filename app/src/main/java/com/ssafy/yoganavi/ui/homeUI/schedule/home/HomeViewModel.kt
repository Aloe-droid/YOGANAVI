package com.ssafy.yoganavi.ui.homeUI.schedule.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.home.HomeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _homeList = MutableStateFlow<List<HomeData>>(emptyList())
    val homeList = _homeList.asStateFlow()

    fun getHomeList(endSession: suspend () -> Unit) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getHomeList() }
            .onSuccess { _homeList.emit(it.data.toMutableList()) }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }
}
