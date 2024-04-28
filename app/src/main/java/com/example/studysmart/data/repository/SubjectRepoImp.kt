package com.example.studysmart.data.repository

import com.example.studysmart.data.local.SubjectDao
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class SubjectRepoImp @Inject constructor(
    private val subjectDao: SubjectDao
): SubjectRepository {
    override suspend fun upsertSubject(subject: Subject) {
        subjectDao.upsertSubject(subject)
    }

    override suspend fun deleteSubject(subject: Subject) {
        TODO("Not yet implemented")
    }

    override suspend fun getSubjectById(id: Int): Subject {
        TODO("Not yet implemented")
    }

    override fun getTotalSubjectCount(): Flow<Int> {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHour(): Flow<Float> {
        return subjectDao.getTotalGoalHour()
    }

    override fun getAllSubjects(): Flow<List<Subject>> {
        return subjectDao.getAllSubjects()
    }
}