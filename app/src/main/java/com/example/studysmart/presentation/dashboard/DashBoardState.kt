package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject

data class DashBoardState(
    val totalSubjectCount:Int = 0,
    val totalStudiedHours:Float = 0f,
    val totalGoalHours:Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectNameTextField: String = "",
    val goalStudyHoursTextField: String = "",
    val colors: List<Color> = Subject.colors.random(),
    val session: Session? = null
)
