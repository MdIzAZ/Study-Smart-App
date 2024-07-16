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

    @Query("delete from subject where subjectId = :subjectId")
    suspend fun deleteSubject (subjectId: Int)

    @Query("SELECT * FROM SUBJECT WHERE SUBJECTID = :id")
    suspend fun getSubjectById(id: Int): Subject


    @Query("select count(*) from subject")
    fun getTotalSubjectCount(): Flow<Int>

    @Query("SELECT SUM(goalHour) FROM SUBJECT")
    fun getTotalGoalHour(): Flow<Float>


    @Query("SELECT * FROM Subject")
    fun getAllSubjects(): Flow<List<Subject>>


}