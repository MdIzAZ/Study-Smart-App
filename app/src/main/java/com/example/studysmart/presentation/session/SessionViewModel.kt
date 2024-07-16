package com.example.studysmart.presentation.session

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.util.SnackBarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject


@HiltViewModel
class SessionViewModel @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val subjectRepository: SubjectRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSession()
    ) { state, subjects, sessions ->

        state.copy(
            subjects = subjects,
            sessions = sessions
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState())

    //snack bar
    private val _snackBarEventFlow =
        MutableSharedFlow<SnackBarEvent>()  //as mutableSharedFlow don't need initial value
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent) {
        when (event) {
            is SessionEvent.OnRelatedToSubjectChanged -> {
                _state.update {
                    it.copy(relatedToSubject = event.sub.name, subjectId = event.sub.subjectId)
                }
            }

            is SessionEvent.SaveSession -> saveSession(event.duration)

            is SessionEvent.OnDeleteSessionButtonClicked -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SessionEvent.DeleteSession -> deleteSession()

            is SessionEvent.NotifyToUpdateSubject -> notifyToUpdateSubject()

            is SessionEvent.UpdateSubjectIdAndRelatedSubject -> {
                _state.update {
                    it.copy(
                        subjectId = event.subjectId,
                        relatedToSubject = event.relatedToSubject
                    )
                }
            }
        }
    }

    private fun notifyToUpdateSubject() {
        viewModelScope.launch {
            if (state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Choose Related to Subject first"
                    )
                )
                return@launch
            }
        }
    }

    private fun saveSession(duration: Long) {

        viewModelScope.launch {
            if (state.value.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Choose Related to Subject first"
                    )
                )
                return@launch
            }
            if (duration < 36) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Session should be at least 36 seconds long"
                    )
                )
                return@launch
            }

            try {
                viewModelScope.launch {
                    val session = Session(
                        sessionSubjectId = state.value.subjectId!!,
                        duration = duration,
                        relatedSub = state.value.relatedToSubject!!,
                        date = Instant.now().toEpochMilli()
                    )

                    sessionRepository.upsertSession(session)

                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            "Session saved successfully"
                        )
                    )

                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = e.message ?: "Failed to save session",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Session deleted successfully"
                    )
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = e.message ?: "Failed to delete session",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

}

