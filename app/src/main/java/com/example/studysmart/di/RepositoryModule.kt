package com.example.studysmart.di

import com.example.studysmart.data.repository.SessionRepoImp
import com.example.studysmart.data.repository.SubjectRepoImp
import com.example.studysmart.data.repository.TaskRepoImp
import com.example.studysmart.domain.repository.SessionRepository
import com.example.studysmart.domain.repository.SubjectRepository
import com.example.studysmart.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {


    @Singleton
    @Binds
    abstract fun bindSubjectRepo(
        subjectRepoImp: SubjectRepoImp
    ): SubjectRepository

    @Singleton
    @Binds
    abstract fun bindSessionRepo(
        sessionRepoImp: SessionRepoImp
    ): SessionRepository

    @Singleton
    @Binds
    abstract fun bindTaskRepo(
        taskRepoImp: TaskRepoImp
    ): TaskRepository

}