package com.example.studysmart.presentation.session

import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject

sealed class SessionEvent {

    data class OnRelatedToSubjectChanged(val sub: Subject) : SessionEvent()

    data class SaveSession(val duration: Long) : SessionEvent()

    data class OnDeleteSessionButtonClicked(val session: Session) : SessionEvent()

    data object DeleteSession : SessionEvent()

    data object NotifyToUpdateSubject : SessionEvent()

    data class UpdateSubjectIdAndRelatedSubject(
        val subjectId: Int?,
        val relatedToSubject: String?,
    ) : SessionEvent()
}