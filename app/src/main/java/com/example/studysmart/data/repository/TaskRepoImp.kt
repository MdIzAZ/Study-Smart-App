package com.example.studysmart.data.repository

import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject



class TaskRepoImp @Inject constructor(
    private val taskDao: TaskDao,
) : TaskRepository {

    override suspend fun upsertTask(task: Tasks) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(task: Tasks) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTaskBySubjectId(subId: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun getTaskById(taskId: Int) {
        TODO("Not yet implemented")
    }

    override fun getAllTasksForSubject(subjectId: Int): Flow<List<Tasks>> {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(): Flow<List<Tasks>> {
        TODO("Not yet implemented")
    }
}