package com.example.studysmart.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.models.Tasks

@Database(
    entities = [Subject::class, Tasks::class, Session::class],
    version = 2
)
@TypeConverters(ColorListConverter::class)
abstract class AppDataBase : RoomDatabase() {

    abstract fun subjectDao(): SubjectDao
    abstract fun taskDao(): TaskDao
    abstract fun sessionDao(): SessionDao

}