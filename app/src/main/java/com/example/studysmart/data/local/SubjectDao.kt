package com.example.studysmart.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmart.domain.models.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Upsert
    suspend fun upsertSubject (subject: Subject)

    @Delete
    suspend fun deleteSubject (subject: Subject)

    @Query("SELECT * FROM SUBJECT WHERE SUBJECTID = :id")
    suspend fun getSubjectById(id: Int): Subject


    @Query("select count(*) from subject")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHour) FROM SUBJECT")
    fun getTotalGoalHour(): Flow<Float>


    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>


}