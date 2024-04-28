package com.example.studysmart.data.repository

import com.example.studysmart.data.local.SessionDao
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class SessionRepoImp @Inject constructor(
    private val sessionDao: SessionDao,
) : SessionRepository {
    override suspend fun upsertSession(session: Session) {
        sessionDao.upsertSession(session)
    }

    override suspend fun deleteSession(session: Session) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllSessionForSubjectId(subId: Int) {
        TODO("Not yet implemented")
    }

    override fun getAllSession(): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getRecentSessionForSubject(subId: Int): Flow<List<Session>> {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionDuration(): Flow<Long> {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionDurationForSubject(subId: Int): Flow<Long> {
        TODO("Not yet implemented")
    }
}