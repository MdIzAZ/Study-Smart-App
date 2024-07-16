package com.example.studysmart.presentation.tasks

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import com.example.studysmart.presentation.navArgs
import com.example.studysmart.util.Priority
import com.example.studysmart.util.SnackBarEvent
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects()
    ) { state, subjects ->

        state.copy(subjects = subjects)

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TaskState())

    //snack bar
    private val _snackBarEventFlow =
        MutableSharedFlow<SnackBarEvent>()  //as mutableSharedFlow don't need initial value
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    //init
    init {
        fetchTask()
        fetchSubject()
    }


    fun onEvent(event: TaskEvent) {
        when (event) {
            is TaskEvent.OnDescriptionChange -> {
                _state.update {
                    it.copy(
                        dscp = event.dscp
                    )
                }
            }

            is TaskEvent.OnTitleChange -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }

            is TaskEvent.OnDateChange -> {
                _state.update {
                    it.copy(
                        dueDate = event.millis
                    )
                }
            }

            is TaskEvent.OnPriorityChange -> {
                _state.update {
                    it.copy(
                        priority = event.priority
                    )
                }
            }

            TaskEvent.OnIsCompleteChange -> {
                _state.update {
                    it.copy(
                        isTaskCompleted = !_state.value.isTaskCompleted
                    )
                }
            }

            is TaskEvent.OnRelatedSubjectSelect -> {
                _state.update {
                    it.copy(
                        subjectId = event.sub.subjectId,
                        relatedToSubject = event.sub.name
                    )
                }
            }

            TaskEvent.SaveTask -> saveTask()

            TaskEvent.DeleteTask -> deleteTask()

        }
    }

    private fun deleteTask() {
        viewModelScope.launch {

            try {
                state.value.currentTaskId?.let {
                    taskRepository.deleteTask(it)
                }
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Task successfully deleted"
                    )
                )

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        "Unable to Delete this task",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }


    private fun saveTask() {
        viewModelScope.launch {
            try {
                val state = _state.value
                if (state.subjectId == null || state.relatedToSubject == null) return@launch

                taskRepository.upsertTask(
                    Tasks(
                        state.title,
                        state.dscp,
                        state.dueDate ?: Instant.now().toEpochMilli(),
                        state.priority.value,
                        state.relatedToSubject,
                        state.isTaskCompleted,
                        state.subjectId,
                        state.currentTaskId
                    )
                )

                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(msg = "New Task created successfully")
                )

            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        msg = e.message ?: "Failed to Create New Task",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch {
            navArgs.taskId?.let {
                taskRepository.getTaskById(it)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            dscp = task.description,
                            dueDate = task.dueDate,
                            priority = Priority.fromInt(task.priority),
                            relatedToSubject = task.relatedSubject,
                            isTaskCompleted = task.isCompleted,
                            subjectId = task.taskSubjectId,
                            currentTaskId = task.taskId
                        )
                    }
                }
            }
        }
    }

    //fetch subject
    private fun fetchSubject() {
        viewModelScope.launch {
            navArgs.subjectId?.let {
                subjectRepository.getSubjectById(it)?.let { subject ->
                    _state.update {
                        it.copy(
                            relatedToSubject = subject.name,
                            subjectId = subject.subjectId
                        )
                    }
                }
            }
        }
    }

}