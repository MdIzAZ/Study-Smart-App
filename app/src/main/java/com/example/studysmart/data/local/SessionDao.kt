package com.example.studysmart.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmart.domain.models.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Upsert
    suspend fun upsertSession(session: Session)

    @Delete
    suspend fun deleteSession(session: Session)

    @Query("delete from session where sessionSubjectId = :subId")
    suspend fun deleteAllSessionForSubjectId(subId: Int)


    @Query("select * from session")
    fun getAllSession(): Flow<List<Session>>

    @Query("select * from session where sessionSubjectId = :subId")
    fun getRecentSessionForSubject(subId: Int): Flow<List<Session>>

    @Query("select sum(duration) from session")
    fun getTotalSessionDuration(): Flow<Long>

    @Query("select sum(duration) from session where sessionSubjectId = :subId")
    fun getTotalSessionDurationForSubject(subId: Int): Flow<Long>

}