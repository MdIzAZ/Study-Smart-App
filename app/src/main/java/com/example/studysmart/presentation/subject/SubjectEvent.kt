package com.example.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Tasks

sealed class SubjectEvent {
    data object UpdateSubject: SubjectEvent()
    data object DeleteSubject: SubjectEvent()
    data object DeleteSession: SubjectEvent()
    data object UpdateProgress: SubjectEvent()
    data class OnCheckBoxClick(val tasks: Tasks): SubjectEvent()
    data class OnSubjectCardColorChange(val colors: List<Color>): SubjectEvent()
    data class OnGoalHoursChange(val hours: String): SubjectEvent()
    data class OnSubjectNameChange(val name: String): SubjectEvent()
    data class OnDeleteSessionButtonClick(val session: Session): SubjectEvent()
}