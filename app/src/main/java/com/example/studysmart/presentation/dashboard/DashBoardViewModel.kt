package com.example.studysmart.presentation.dashboard

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.util.SnackBarEvent
import com.example.studysmart.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashBoardViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashBoardState())

    // states -> sub count, studied hour , goal hour, subject list
    val state = combine(
        _state,
        subjectRepository.getTotalSubjectCount(),
        subjectRepository.getTotalGoalHour(),
        subjectRepository.getAllSubjects(),
        sessionRepository.getTotalSessionDuration()
    ) { state, subCount, goal, subjects, totalSessionDuration ->

        state.copy(
            totalSubjectCount = subCount,
            totalGoalHours = goal,
            subjects = subjects,
            totalStudiedHours = totalSessionDuration.toHours()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashBoardState())

    //swtate to manage tasks
    val tasks: StateFlow<List<Tasks>> = taskRepository.getAllUpcomingTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //state to manage sessions
    val sessions: StateFlow<List<Session>> = sessionRepository.getRecentFiveSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    //snack bar
    private val _snackBarEventFlow =
        MutableSharedFlow<SnackBarEvent>()  //as mutableSharedFlow don't need initial value
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()


    fun onEvent(event: DashBoardEvent) {
        when (event) {
            DashBoardEvent.DeleteSession -> deleteSession()
            is DashBoardEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is DashBoardEvent.OnGoalStudyHourChange -> {
                _state.update {
                    it.copy(goalStudyHoursTextField = event.hours)
                }
            }

            is DashBoardEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(colors = event.colors)
                }
            }

            is DashBoardEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(subjectNameTextField = event.name)
                }
            }

            is DashBoardEvent.OnTaskIsCompleteChange -> updateTaskIsComplete(event.task)
            DashBoardEvent.SaveSubject -> saveSubject()
        }

    }

    private fun updateTaskIsComplete(task: Tasks) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task.copy(isCompleted = !task.isCompleted))

                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = "task is marked as ${ if(task.isCompleted)  "incomplete" else "completed" }"
                    )
                )

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = e.message ?: "Failed to change task details try again",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun saveSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    Subject(
                        name = state.value.subjectNameTextField,
                        goalHour = state.value.goalStudyHoursTextField.toFloatOrNull() ?: 1f,
                        colors = state.value.colors.map {
                            it.toArgb()
                        }
                    )
                )
                _state.update {
                    it.copy(
                        subjectNameTextField = "",
                        goalStudyHoursTextField = ""
                    )
                }

                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = "Subject Card created successfully"
                    )
                )

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = e.message ?: "Failed to Save New Subject try again",
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