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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.CountCard
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.studySessionList
import com.example.studysmart.presentation.components.taskList
import com.example.studysmart.presentation.destinations.TaskScreenDestination
import com.example.studysmart.presentation.tasks.TaskScreenNavArgs
import com.example.studysmart.presentation.theme.gradient1
import com.example.studysmart.sessionList
import com.example.studysmart.tasks
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


data class SubjectScreenNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectScreen(
    navigator: DestinationsNavigator
) {
    val isOpened = rememberSaveable { mutableStateOf(false) }
    val selectedColor = remember { mutableStateOf(gradient1) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState(0)
    val isFabIconExpanded = remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    val subjectNameState = remember { mutableStateOf("subjectName") }
    val goalHour = remember { mutableStateOf("") }

    val isSessionDeleteDialogOpened = rememberSaveable { mutableStateOf(false) }
    val isSubjectDeleteDialogOpened = rememberSaveable { mutableStateOf(false) }


    AddSubjectDialog(
        isOpened = isOpened.value,
        title = "Update Subject",
        onDismissRequest = { isOpened.value = !isOpened.value },
        onConfirmButtonClick = {},
        selectedColor = selectedColor.value,
        subjectName = subjectNameState.value,
        goalHour = goalHour.value,
        onGoalHourChange = { goalHour.value = it },
        onSubjectNameChange = {subjectNameState.value = it},
        onSelectedColorChange = {selectedColor.value = it}
    )

    DeleteDialog(
        isOpened = isSessionDeleteDialogOpened.value,
        title = "Delete Session",
        bodyText = "Are you sure?",
        onDismissRequest = { isSessionDeleteDialogOpened.value = false }
    ) {
        isSessionDeleteDialogOpened.value = false
    }

    DeleteDialog(
        isOpened = isSubjectDeleteDialogOpened.value,
        title = "Subject Session",
        bodyText = "Are you sure?",
        onDismissRequest = { isSubjectDeleteDialogOpened.value = false }
    ) {
        isSubjectDeleteDialogOpened.value = false
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubScreenTopBar(
                title = "subjectName",
                onArrowBackClick = {
                    navigator.navigateUp()
                },
                onDeleteIconClick = { isSubjectDeleteDialogOpened.value = true },
                scrollBehavior = scrollBehavior,
                onEditIconClick = { isOpened.value = true}
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigator.navigate(TaskScreenDestination(TaskScreenNavArgs(null, null))) },
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
                    studiedHours = "5",
                    goalHour = "8",
                    progressc = .75f
                )
            }
            taskList(
                sectionTitle = "INCOMPLETE TASKS",
                tasks,
                "You have no task \n Click + Button to Add",
                onCheckBoxClick = { },
                onTaskCardClick = {
                    val args = TaskScreenNavArgs(taskId = it)
                    navigator.navigate(TaskScreenDestination(args))
                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            taskList(
                sectionTitle = "COMPLETED TASKS",
                emptyList(),
                "You have no completed task \n Click check box to Add",
                onCheckBoxClick = { },
                onTaskCardClick = {}
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            studySessionList(
                "RECENT STUDY SESSIONS",
                sessionList,
                "No Recent Session",
                {

                }
            ) {
                isSessionDeleteDialogOpened.value = true
            }

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
    scrollBehavior: TopAppBarScrollBehavior
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
    progressc: Float,
) {
    val progress: Float = studiedHours.toFloat()/goalHour.toFloat()
    val progressPercentage = remember(progress) { (progress * 100).toInt().coerceIn(0, 100) }

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

            Text(text = "$progressPercentage %")
        }
    }
}