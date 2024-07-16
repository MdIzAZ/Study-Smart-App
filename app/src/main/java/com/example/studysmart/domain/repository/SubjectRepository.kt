package com.example.studysmart.domain.repository

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmart.domain.models.Subject
import kotlinx.coroutines.flow.Flow

interface SubjectRepository {

    suspend fun upsertSubject (subject: Subject)

    suspend fun deleteSubject (subjectId: Int)

    suspend fun getSubjectById(id: Int): Subject?

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHour(): Flow<Float>

    fun getAllSubjects(): Flow<List<Subject>>

}