package com.example.studysmart.presentation.session

import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: Int? = null,
    val session: Session? = null,
)
