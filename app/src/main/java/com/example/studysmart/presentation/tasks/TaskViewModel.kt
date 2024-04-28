package com.example.studysmart.presentation.tasks

import androidx.lifecycle.ViewModel
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    taskRepository: TaskRepository
): ViewModel() {

}