package com.example.studysmart.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.presentation.navArgs
import com.example.studysmart.util.SnackBarEvent
import com.example.studysmart.util.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    //fetching nav args
    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getAllInCompleteTasksForSubject(navArgs.subjectId),
        taskRepository.getAllCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionDurationForSubject(navArgs.subjectId)

    ) { state, inComplete, completed, recentSessions, totalDuration ->

        state.copy(
            inCompleteTasks = inComplete,
            completedTasks = completed,
            recentSessions = recentSessions,
            studiedHour = totalDuration.toHours()
        )

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SubjectState())


    //snack bar
    private val _snackBarEventFlow =
        MutableSharedFlow<SnackBarEvent>()  //as mutableSharedFlow don't need initial value
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()


    init {
        fetchSubject()
    }


    fun onEvent(event: SubjectEvent) {
        when (event) {
            SubjectEvent.DeleteSession -> deleteSession()
            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }
            is SubjectEvent.OnGoalHoursChange -> {
                _state.update {
                    it.copy(
                        goalStudyHour = event.hours
                    )
                }
            }

            is SubjectEvent.OnSubjectCardColorChange -> {
                _state.update {
                    it.copy(
                        subCardColors = event.colors
                    )
                }
            }

            is SubjectEvent.OnSubjectNameChange -> {
                _state.update {
                    it.copy(
                        subjectName = event.name
                    )
                }
            }

            is SubjectEvent.OnCheckBoxClick -> updateTaskIsComplete(event.tasks)

            SubjectEvent.UpdateSubject -> updateSubject()

            SubjectEvent.UpdateProgress -> {
                val goal = state.value.goalStudyHour.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(progress = (state.value.studiedHour / goal).coerceIn(0f, 1f))
                }
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {

            try {
                state.value.currentSubjectId?.let {
                    subjectRepository.deleteSubject(it)
                }
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Subject card successfully deleted"
                    )
                )
                delay(2000)

                _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Unable to Delete this card",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHour = state.value.goalStudyHour.toFloatOrNull() ?: 0f,
                        colors = state.value.subCardColors.map { it.toArgb() }
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(msg = "Subject card updated successfully")
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        e.message ?: "Failed to update",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository
                .getSubjectById(navArgs.subjectId)?.let { sub ->
                    _state.update { it ->
                        it.copy(
                            subjectName = sub.name,
                            goalStudyHour = sub.goalHour.toString(),
                            subCardColors = sub.colors.map { Color(it) },
                            currentSubjectId = sub.subjectId
                        )
                    }
                }

        }
    }


    private fun updateTaskIsComplete(task: Tasks) {

        viewModelScope.launch {
            try {
                taskRepository.upsertTask(task.copy(isCompleted = !task.isCompleted))

                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = "task is marked as ${if (task.isCompleted) "incomplete" else "completed"}"
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