package com.example.studysmart.presentation.dashboard

import androidx.compose.ui.graphics.Color
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Tasks

sealed class DashBoardEvent {
    data object SaveSubject: DashBoardEvent()

    data object DeleteSession: DashBoardEvent()

    data class OnDeleteSessionButtonClick(val session : Session) : DashBoardEvent()

    data class OnTaskIsCompleteChange(val task : Tasks) : DashBoardEvent()

    data class OnSubjectCardColorChange(val colors : List<Color>) : DashBoardEvent()

    data class OnGoalStudyHourChange(val hours : String) : DashBoardEvent()

    data class OnSubjectNameChange(val name : String) : DashBoardEvent()
}