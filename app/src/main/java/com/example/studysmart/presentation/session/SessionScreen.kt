package com.example.studysmart.presentation.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.RelatedToSubjectSection
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.studySessionList
import com.example.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import com.example.studysmart.util.Constants.ACTION_SERVICE_START
import com.example.studysmart.util.Constants.ACTION_SERVICE_STOP
import com.example.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "study_smart://session"
        )
    ],
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navigator: DestinationsNavigator,
    timerService: TimerService,
) {
    val viewModel = hiltViewModel<SessionViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = viewModel::onEvent


    //service
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState


    val context = LocalContext.current

    //states for bottom sheet
    var isBottomSheetOpened by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedSubject by remember { mutableStateOf("English") }

    //states for delete Dialog
    var isDeleteDialogOpened by remember { mutableStateOf(false) }

    //snack bar
    val snackBarEvent: SharedFlow<SnackBarEvent> = viewModel.snackBarEventFlow
    val snackBarHostState = remember { SnackbarHostState() }
    //launched effect
    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when (event) {
                is SnackBarEvent.ShowSnackBar -> {
                    snackBarHostState.showSnackbar(message = event.msg, duration = event.duration)
                }

                SnackBarEvent.NavigateUp -> {}
            }
        }
    }

    LaunchedEffect(key1 = state.subjects) {
        val subjectId = timerService.subjectId.value
        onEvent(
            SessionEvent.UpdateSubjectIdAndRelatedSubject(
                subjectId,
                state.subjects.find { it.subjectId == subjectId }?.name
            )
        )
    }




    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpened = isBottomSheetOpened,
        subjects = state.subjects,
        onSubjectClicked = { subject ->
            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                selectedSubject = subject.name
                isBottomSheetOpened = false
            }
            onEvent(SessionEvent.OnRelatedToSubjectChanged(subject))
        },
        onDismissRequest = { isBottomSheetOpened = false }
    )

    DeleteDialog(
        isOpened = isDeleteDialogOpened,
        title = "Delete Session?",
        bodyText = "Are you sure want to delete this session?",
        onDismissRequest = { isDeleteDialogOpened = false },
        onConfirmButtonClick = {
            onEvent(SessionEvent.DeleteSession)
            isDeleteDialogOpened = false
        }
    )


    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            SessionTopBar { navigator.navigateUp() }
        }
    ) { it ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            item {
                TimerSection(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    RelatedToSubjectSection(selectedSubject = state.relatedToSubject ?: "") {
                        isBottomSheetOpened = true
                    }
                }

            }

            item {
                ButtonSection(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    onStartButtonClick = {
                        if (state.subjectId != null && state.relatedToSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context,
                                if (currentTimerState == TimerState.RUNNING)
                                    ACTION_SERVICE_STOP
                                else ACTION_SERVICE_START
                            )
                            timerService.subjectId.value = state.subjectId
                        } else {
                            onEvent(SessionEvent.NotifyToUpdateSubject)
                        }


                    },
                    onCancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context,
                            ACTION_SERVICE_CANCEL
                        )
                    },
                    onFinishButtonClick = {
                        val duration: Long = timerService.duration.toLong(DurationUnit.SECONDS)
                        onEvent(SessionEvent.SaveSession(duration))
                        if (duration >= 36 && state.relatedToSubject != null) {
                            ServiceHelper.triggerForegroundService(
                                context,
                                ACTION_SERVICE_CANCEL
                            )
                        }
                    },
                    timerState = currentTimerState,
                    seconds = seconds
                )
            }

            studySessionList(
                "STUDY SESSIONS HISTORY",
                state.sessions,
                "No Recent Session",
                onDeleteIconClick = {
                    isDeleteDialogOpened = true
                    onEvent(SessionEvent.OnDeleteSessionButtonClicked(it))
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionTopBar(
    onBackButtonClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "Study Session", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "navigate back")
            }
        }
    )
}


@Composable
fun TimerSection(
    modifier: Modifier = Modifier,
    hours: String,
    minutes: String,
    seconds: String,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(5.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        )

        Row {
            AnimatedContent(
                targetState = hours,
                label = hours,
                transitionSpec = { timerTextAnimation() }) { h ->
                Text(
                    text = "$h:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(
                targetState = minutes,
                label = minutes,
                transitionSpec = { timerTextAnimation() }) { m ->
                Text(
                    text = "$m:",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
            AnimatedContent(
                targetState = seconds,
                label = seconds,
                transitionSpec = { timerTextAnimation() }) { s ->
                Text(
                    text = "$s",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                )
            }
        }

    }
}


@Composable
fun ButtonSection(
    modifier: Modifier,
    onStartButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit,
    onFinishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String,

    ) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = onCancelButtonClick,
            enabled = seconds != "00"
        ) {
            Text(modifier = Modifier.padding(10.dp, 5.dp), text = "Cancel")
        }
        Button(
            onClick = onStartButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor =
                if (timerState == TimerState.RUNNING) MaterialTheme.colorScheme.tertiary
                else if (timerState == TimerState.CANCELLED) Color.Green
                else Color.Magenta
            )
        ) {
            Text(
                modifier = Modifier.padding(10.dp, 5.dp),
                text = when (timerState) {
                    TimerState.RUNNING -> "Pause"
                    TimerState.PAUSED -> "Resume"
                    else -> "Start"
                }
            )
        }
        Button(
            onClick = onFinishButtonClick,
            enabled = seconds != "00"
        ) {
            Text(modifier = Modifier.padding(10.dp, 5.dp), text = "Finish")
        }
    }
}


private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(animationSpec = tween(duration)) { fullHeight -> fullHeight } +
            fadeIn(animationSpec = tween(duration)) togetherWith
            slideOutVertically(animationSpec = tween(duration)) { fullHeight -> -fullHeight } +
            fadeOut()
}





