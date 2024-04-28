package com.example.studysmart.presentation.dashboard


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.studysmart.R
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.list
import com.example.studysmart.presentation.components.AddSubjectDialog
import com.example.studysmart.presentation.components.CountCard
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.SubjectCard
import com.example.studysmart.presentation.components.studySessionList
import com.example.studysmart.presentation.components.taskList
import com.example.studysmart.presentation.destinations.SessionScreenDestination
import com.example.studysmart.presentation.destinations.SubjectScreenDestination
import com.example.studysmart.presentation.destinations.TaskScreenDestination
import com.example.studysmart.presentation.subject.SubjectScreenNavArgs
import com.example.studysmart.presentation.tasks.TaskScreenNavArgs
import com.example.studysmart.sessionList
import com.example.studysmart.tasks
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@Destination(start = true)
@Composable
fun DashBoardScreen(
    navigator: DestinationsNavigator,
) {
    //view model
    val viewModel = hiltViewModel<DashBoardViewModel>()
    //state
    val state by viewModel.state.collectAsStateWithLifecycle()

    val isAddSubjectDialogOpened = rememberSaveable { mutableStateOf(false) }
    val isDeleteDialogOpened = rememberSaveable { mutableStateOf(false) }





    AddSubjectDialog(
        isOpened = isAddSubjectDialogOpened.value,
        title = "Add/Update New Subject",
        onDismissRequest = { isAddSubjectDialogOpened.value = !isAddSubjectDialogOpened.value },
        onConfirmButtonClick = {},
        selectedColor = state.colors,
        subjectName = state.subjectNameTextField,
        goalHour = state.goalStudyHoursTextField,
        onGoalHourChange = { },
        onSubjectNameChange = { },
        onSelectedColorChange = { }
    )

    DeleteDialog(
        isOpened = isDeleteDialogOpened.value,
        title = "Delete Session",
        bodyText = "Are you sure?",
        onDismissRequest = { isDeleteDialogOpened.value = false }
    ) {
        isDeleteDialogOpened.value = false
    }


    Scaffold(
        topBar = { DashBoardScreenTopBar() }
    ) { it ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                CountCardSection(
                    subjectCount = state.totalSubjectCount,
                    studiedHour = state.totalStudiedHours.toString(),
                    goalHour = state.totalGoalHours.toString()
                )
            }

            item {
                SubjectCardSection(
                    modifier = Modifier,
                    subjectList = state.subjects,
                    onPlusButtonClick = { isAddSubjectDialogOpened.value = true },
                    onSubjectCardClick = {
                        val navArg = SubjectScreenNavArgs(it)
                        navigator.navigate(SubjectScreenDestination(navArg))
                    }
                )
            }

            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp, 20.dp),
                    onClick = {
                        navigator.navigate(SessionScreenDestination())
                    }
                ) { Text(text = "Start Study Session") }
            }

            taskList(
                sectionTitle = "UPCOMING TASKS",
                tasks,
                "You have no task \n Click + Button to Add",
                onCheckBoxClick = { },
                onTaskCardClick = {
                    val navArg = TaskScreenNavArgs(taskId = it, subjectId = null)
                    navigator.navigate(TaskScreenDestination(navArg))
                }
            )
            item { Spacer(modifier = Modifier.height(20.dp)) }
            studySessionList(
                "RECENT STUDY SESSIONS",
                sessionList,
                "No Recent Session",
                {}
            ) {
                isDeleteDialogOpened.value = true
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashBoardScreenTopBar() {
    CenterAlignedTopAppBar(title = {
        Text(
            text = "StudySmart",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    })
}


@Composable
fun CountCardSection(
    modifier: Modifier = Modifier,
    subjectCount: Int,
    studiedHour: String,
    goalHour: String,
) {

    Row(
        modifier = modifier
    ) {
        CountCard(
            heading = "Subject Count",
            count = "$subjectCount",
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(heading = "Studied Hour", count = studiedHour, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))

        CountCard(heading = "Goal", count = goalHour, modifier = Modifier.weight(1f))
    }
}


@Composable
private fun SubjectCardSection(
    modifier: Modifier,
    subjectList: List<Subject>,
    onPlusButtonClick: () -> Unit,
    onSubjectCardClick: (Int) -> Unit,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp),
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onPlusButtonClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp)
                )
            }
        }

        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.img_books),
                contentDescription = null
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "No Subject added Yet",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) {
                SubjectCard(
                    subjectName = it.name,
                    gradientColor = it.colors.map { Color(it) },
                    onClick = {
                        it.subjectId?.let { it1 -> onSubjectCardClick(it1) }
                    }
                )
            }
        }
    }
}





















