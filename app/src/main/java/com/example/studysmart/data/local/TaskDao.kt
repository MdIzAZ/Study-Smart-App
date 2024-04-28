package com.example.studysmart.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.studysmart.domain.models.Tasks
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Upsert
    suspend fun upsertTask(task: Tasks)

    @Delete
    suspend fun deleteTask(task: Tasks)

    @Query("delete from tasks where taskSubjectId = :subId")
    suspend fun deleteTaskBySubjectId(subId: Int)

    @Query("select * from tasks where taskSubjectId = :taskId")
    suspend fun getTaskById(taskId: Int) : Tasks

    @Query("select * from tasks where taskSubjectId = :subjectId")
    fun getAllTasksForSubject(subjectId: Int) : Flow<List<Tasks>>

    @Query("select * from tasks")
    fun getAllTasks():  Flow<List<Tasks>>
}