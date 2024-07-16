package com.example.studysmart.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tasks(

    val title: String,
    val description: String,
    val dueDate: Long,
    val priority: Int,
    val relatedSubject: String,
    val isCompleted: Boolean,

    val taskSubjectId: Int,

    @PrimaryKey(autoGenerate = true)
    val taskId: Int? = null,
)
