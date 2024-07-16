package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.models.Tasks

data class SubjectState(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val goalStudyHour: String = "",
    val studiedHour: Float = 0f,
    val subCardColors: List<Color> = Subject.colors.random(),
    val recentSessions : List<Session> = emptyList(),
    val inCompleteTasks: List<Tasks> = emptyList(),
    val completedTasks: List<Tasks> = emptyList(),
    val session: Session? = null,
    val progress: Float = 0f,
    val isLoading: Boolean = false
)
