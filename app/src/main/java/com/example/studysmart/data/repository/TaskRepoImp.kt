package com.example.studysmart.data.repository

import com.example.studysmart.data.local.TaskDao
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class TaskRepoImp @Inject constructor(
    private val taskDao: TaskDao,
) : TaskRepository {

    override suspend fun upsertTask(task: Tasks) {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int) {
        taskDao.deleteTask(taskId)
    }

    override suspend fun deleteTaskBySubjectId(subId: Int) {
        taskDao.deleteTaskBySubjectId(subId)
    }

    override suspend fun getTaskById(taskId: Int): Tasks {
        return taskDao.getTaskById(taskId)
    }

    override fun getAllInCompleteTasksForSubject(subjectId: Int): Flow<List<Tasks>> {
        return taskDao.getAllTasksForSubject(subjectId)
            .map { list ->
                list.filter {
                    it.isCompleted.not()
                }
            }
            .map { list ->
                list.sortedWith(compareBy<Tasks> { it.dueDate }.thenByDescending { it.priority })
            }
    }

    override fun getAllCompletedTasksForSubject(subjectId: Int): Flow<List<Tasks>> {
        return taskDao.getAllTasksForSubject(subjectId)
            .map { list ->
                list.filter {
                    it.isCompleted
                }
            }
            .map { list ->
                list.sortedWith(compareBy<Tasks> { it.dueDate }.thenByDescending { it.priority })
            }
    }

    override fun getAllUpcomingTasks(): Flow<List<Tasks>> {
        return taskDao.getAllTasks()
            // filter -> not completed taks
            .map { list ->
                list.filter {
                    it.isCompleted.not()  // which tasks are not completed
                }
            }
            // sort -> by priority in descending order
            .map { list ->
                list.sortedWith(compareBy<Tasks> { it.dueDate }.thenByDescending { it.priority })
            }
    }
}