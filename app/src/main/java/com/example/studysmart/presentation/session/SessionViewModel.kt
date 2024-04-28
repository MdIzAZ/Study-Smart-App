package com.example.studysmart.presentation.session

import androidx.lifecycle.ViewModel
import com.example.studysmart.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionRepository: SessionRepository
): ViewModel() {

}