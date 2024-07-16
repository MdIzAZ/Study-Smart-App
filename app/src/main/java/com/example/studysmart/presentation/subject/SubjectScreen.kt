package com.example.studysmart.presentation.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.CountCard
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.studySessionList
import com.example.studysmart.presentation.components.taskList
import com.example.studysmart.presentation.destinations.TaskScreenDestination
import com.example.studysmart.presentation.tasks.TaskScreenNavArgs
import com.example.studysmart.presentation.theme.gradient1
import com.example.studysmart.util.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest


data class SubjectScreenNavArgs(
    val subjectId: Int,
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    navigator: DestinationsNavigator,
) {

    //view model
    val viewModel = hiltViewModel<SubjectViewModel>()
    val onEvent = viewModel::onEvent

    //states
    val state by viewModel.state.collectAsStateWithLifecycle()

    //local states
    var isUpdateSubjectDialogOpened by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState(0)
    val isFabIconExpanded = remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    var isSessionDeleteDialogOpened by rememberSaveable { mutableStateOf(false) }
    var isSubjectDeleteDialogOpened by rememberSaveable { mutableStateOf(false) }

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

                SnackBarEvent.NavigateUp -> {
                    navigator.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(key1 = state.studiedHour, key2 = state.goalStudyHour) {
        onEvent(SubjectEvent.UpdateProgress)
    }


    AddSubjectDialog(
        isOpened = isUpdateSubjectDialogOpened,
        title = "Update Subject Card",
        onDismissRequest = { isUpdateSubjectDialogOpened = false },
        selectedColor = state.subCardColors,
        subjectName = state.subjectName,
        goalHour = state.goalStudyHour,
        onGoalHourChange = { onEvent(SubjectEvent.OnGoalHoursChange(it)) },
        onSubjectNameChange = { onEvent(SubjectEvent.OnSubjectNameChange(it)) },
        onSelectedColorChange = { onEvent(SubjectEvent.OnSubjectCardColorChange(it)) },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isUpdateSubjectDialogOpened = false
        }
    )

    DeleteDialog(
        isOpened = isSessionDeleteDialogOpened,
        title = "Delete Session",
        bodyText = "Are you sure?",
        onDismissRequest = { isSessionDeleteDialogOpened = false }
    ) {
        onEvent(SubjectEvent.DeleteSession)
        isSessionDeleteDialogOpened = false
    }

    DeleteDialog(
        isOpened = isSubjectDeleteDialogOpened,
        title = "Subject Session",
        bodyText = "Are you sure?",
        onDismissRequest = { isSubjectDeleteDialogOpened = false }
    ) {
        onEvent(SubjectEvent.DeleteSubject)
        isSubjectDeleteDialogOpened = false
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) },
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubScreenTopBar(
                title = state.subjectName,
                onArrowBackClick = {
                    navigator.navigateUp()
                },
                onDeleteIconClick = { isSubjectDeleteDialogOpened = true },
                scrollBehavior = scrollBehavior,
                onEditIconClick = { isUpdateSubjectDialogOpened = true }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    navigator.navigate(
                        TaskScreenDestination(
                            TaskScreenNavArgs(
                                state.currentSubjectId,
                                -1
                            )
                        )
                    )
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add Tasks") },
                text = { Text(text = "Add Tasks") },
                expanded = isFabIconExpanded.value
            )
        }
    ) { it ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier,
                    studiedHours = state.studiedHour.toString(),
                    goalHour = state.goalStudyHour,
                    progress = state.progress
                )
            }
            taskList(
                sectionTitle = "INCOMPLETE TASKS",
                state.inCompleteTasks,
                "You have no task \n Click + Button to Add",
                onCheckBoxClick = { onEvent(SubjectEvent.OnCheckBoxClick(it)) },
                onTaskCardClick = {
                    val args = TaskScreenNavArgs(taskId = it)
                    navigator.navigate(TaskScreenDestination(args))
                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            taskList(
                sectionTitle = "COMPLETED TASKS",
                state.completedTasks,
                "You have no completed task \n Click check box to Add",
                onCheckBoxClick = { onEvent(SubjectEvent.OnCheckBoxClick(it)) },
                onTaskCardClick = {
                    val args = TaskScreenNavArgs(taskId = it)
                    navigator.navigate(TaskScreenDestination(args))
                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            studySessionList(
                "RECENT STUDY SESSIONS",
                state.recentSessions,
                "No Recent Session",
                onDeleteIconClick = {
                    isSessionDeleteDialogOpened = true
                    onEvent(SubjectEvent.OnDeleteSessionButtonClick(it))
                }
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubScreenTopBar(
    title: String,
    onArrowBackClick: () -> Unit,
    onDeleteIconClick: () -> Unit,
    onEditIconClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    LargeTopAppBar(

        scrollBehavior = scrollBehavior,
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onArrowBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back to")
            }
        },
        actions = {
            IconButton(onClick = onDeleteIconClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Subject")
            }
            IconButton(onClick = onEditIconClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Subject")
            }
        }
    )
}

@Composable
fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHour: String,
    progress: Float,
) {
    val percentageProgress = remember(key1 = progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(modifier = Modifier.weight(1f), heading = "Goal Study Hours", count = goalHour)
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(modifier = Modifier.weight(1f), heading = "Study Hours", count = studiedHours)
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(75.dp), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )

            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )

            Text(text = "$percentageProgress %")
        }
    }
}