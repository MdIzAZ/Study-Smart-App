package com.example.studysmart.domain.repository


import com.example.studysmart.domain.models.Tasks
import kotlinx.coroutines.flow.Flow

interface TaskRepository {


    suspend fun upsertTask(task: Tasks)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int) : Tasks

    suspend fun deleteTaskBySubjectId(subId: Int)

    fun getAllInCompleteTasksForSubject(subjectId: Int) : Flow<List<Tasks>>

    fun getAllCompletedTasksForSubject(subjectId: Int) : Flow<List<Tasks>>

    fun getAllUpcomingTasks(): Flow<List<Tasks>>

}