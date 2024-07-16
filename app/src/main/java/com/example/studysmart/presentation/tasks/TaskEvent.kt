package com.example.studysmart.presentation.tasks

import com.example.studysmart.domain.models.Subject
import com.example.studysmart.util.Priority

sealed class TaskEvent {

    data class OnTitleChange(val title: String) : TaskEvent()

    data class OnDescriptionChange(val dscp: String) : TaskEvent()

    data class OnDateChange(val millis: Long?) : TaskEvent()

    data class OnPriorityChange(val priority: Priority) : TaskEvent()

    data class OnRelatedSubjectSelect(val sub: Subject) : TaskEvent()

    data object OnIsCompleteChange : TaskEvent()

    data object SaveTask : TaskEvent()

    data object DeleteTask : TaskEvent()

}