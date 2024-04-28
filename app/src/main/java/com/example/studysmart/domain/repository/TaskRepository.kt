package com.example.studysmart.domain.repository


import com.example.studysmart.domain.models.Tasks
import kotlinx.coroutines.flow.Flow

interface TaskRepository {


    suspend fun upsertTask(task: Tasks)

    suspend fun deleteTask(task: Tasks)

    suspend fun deleteTaskBySubjectId(subId: Int)

    suspend fun getTaskById(taskId: Int)

    fun getAllTasksForSubject(subjectId: Int) : Flow<List<Tasks>>

    fun getAllTasks(): Flow<List<Tasks>>

}