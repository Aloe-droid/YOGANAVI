package com.ssafy.yoganavi.ui.homeUI.myPage.registerLive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.repository.response.AuthException
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import com.ssafy.yoganavi.ui.utils.Week
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterLiveViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {

    var liveLectureData = LiveLectureData(teacherProfile = "", teacherSmallProfile = "")
    var dayStatusMap = Week.entries.associateWith { false }.toMutableMap()

    fun getLive(
        liveId: Int,
        onReadLive: () -> Unit,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.getLive(liveId) }
            .onSuccess {
                it.data?.let { data ->
                    liveLectureData = data

                    liveLectureData.availableDay.split(",").forEach { parseDayStr ->
                        val parseDayWeek = Week.valueOf(parseDayStr)
                        dayStatusMap[parseDayWeek] = true
                    }

                    onReadLive()
                }
            }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun createLive(
        popBack: suspend () -> Unit,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.createLive(liveLectureData) }
            .onSuccess { popBack() }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }

    fun updateLive(
        popBack: suspend () -> Unit,
        endSession: suspend () -> Unit
    ) = viewModelScope.launch(Dispatchers.IO) {
        runCatching { infoRepository.updateLive(liveLectureData, liveLectureData.liveId) }
            .onSuccess { popBack() }
            .onFailure { (it as? AuthException)?.let { endSession() } ?: it.printStackTrace() }
    }
}
