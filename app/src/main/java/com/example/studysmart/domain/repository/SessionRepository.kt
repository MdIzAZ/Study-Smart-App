package com.example.studysmart.domain.repository

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmart.domain.models.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    suspend fun upsertSession(session: Session)

    suspend fun deleteSession(session: Session)

    suspend fun deleteAllSessionForSubjectId(subId: Int)

    fun getAllSession(): Flow<List<Session>>

    fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>>

    fun getRecentFiveSessions(): Flow<List<Session>>

    fun getTotalSessionDuration(): Flow<Long>

    fun getTotalSessionDurationForSubject(subId: Int): Flow<Long>
}