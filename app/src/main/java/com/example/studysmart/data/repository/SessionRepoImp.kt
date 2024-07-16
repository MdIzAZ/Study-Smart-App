package com.example.studysmart.data.repository

import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject


class SessionRepoImp @Inject constructor(
    private val sessionDao: SessionDao,
) : SessionRepository {

    override suspend fun upsertSession(session: Session) {
        sessionDao.upsertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        sessionDao.deleteSession(session)
    }

    override suspend fun deleteAllSessionForSubjectId(subId: Int) {
        return sessionDao.deleteAllSessionForSubjectId(subId)
    }

    override fun getAllSession(): Flow<List<Session>> {
        return sessionDao.getAllSession()
            .map { list ->
                list.sortedByDescending { it.date }
            }
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>> {
        return sessionDao.getRecentSessionForSubject(subjectId)
            .map { list ->
                list.sortedByDescending { it.date }
            }
            .take(10)
    }

    override fun getRecentFiveSessions(): Flow<List<Session>> {
        return sessionDao.getAllSession()
            .map { list ->
                list.sortedByDescending { it.date }
            }
            .take(5)
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationForSubject(subId: Int): Flow<Long> {
        return sessionDao.getTotalSessionDurationForSubject(subId)
    }
}