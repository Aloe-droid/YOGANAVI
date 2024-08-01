package com.ssafy.yoganavi.ui.homeUI.teacher.teacherReservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.yoganavi.data.repository.info.InfoRepository
import com.ssafy.yoganavi.data.source.dto.live.LiveLectureData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherReservationViewModel @Inject constructor(
    private val infoRepository: InfoRepository
) : ViewModel() {
    private val _availableList = MutableStateFlow<List<LiveLectureData>>(emptyList())
    val availableList = _availableList.asStateFlow()

    fun getAvailableClass(teacherId: Int, method: Int) = viewModelScope.launch {
        runCatching { infoRepository.getAvailableClass(teacherId, method) }
            .onSuccess { _availableList.emit(it.data.toMutableList()) }
            .onFailure { it.printStackTrace() }
    }
}
