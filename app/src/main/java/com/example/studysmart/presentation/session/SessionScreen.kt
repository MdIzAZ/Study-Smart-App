package com.example.studysmart.presentation.session

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.list
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.RelatedToSubjectSection
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.studySessionList
import com.example.studysmart.sessionList
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Destination
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    navigator: DestinationsNavigator,
) {

    val viewModel = hiltViewModel<SessionViewModel>()

    //states for bottom sheet
    var isBottomSheetOpened by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedSubject by remember { mutableStateOf("English") }

    //states for delete Dialog
    var isDeleteDialogOpened by remember { mutableStateOf(false) }




    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpened = isBottomSheetOpened,
        subjects = list,
        onSubjectClicked = { subject ->
            scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                selectedSubject = subject.name
                isBottomSheetOpened = false
            }
        },
        onDismissRequest = { isBottomSheetOpened = false }
    )

    DeleteDialog(
        isOpened = isDeleteDialogOpened,
        title = "Delete Session?",
        bodyText = "Are you sure want to delete this session?",
        onDismissRequest = { isDeleteDialogOpened = false }) {

    }


    Scaffold(
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
                        .aspectRatio(1f)
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    RelatedToSubjectSection(selectedSubject = selectedSubject) {
                        isBottomSheetOpened = true
                    }
                }

            }

            item {
                ButtonSection(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    onStartButtonClick = { /*TODO*/ },
                    onCancelButtonClick = { /*TODO*/ },
                    onPauseButtonClick = {}
                )
            }

            studySessionList(
                "STUDY SESSIONS HISTORY",
                sessionList,
                "No Recent Session",
                {}
            ) {
                isDeleteDialogOpened = true
            }
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

        Text(text = "00:05:32", style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp))
    }
}


@Composable
fun ButtonSection(
    modifier: Modifier,
    onStartButtonClick: () -> Unit,
    onCancelButtonClick: () -> Unit,
    onPauseButtonClick: () -> Unit,

    ) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(onClick = onCancelButtonClick) {
            Text(modifier = Modifier.padding(10.dp, 5.dp), text = "Cancel")
        }
        Button(onClick = onStartButtonClick) {
            Text(modifier = Modifier.padding(10.dp, 5.dp), text = "Start")
        }
        Button(onClick = onPauseButtonClick) {
            Text(modifier = Modifier.padding(10.dp, 5.dp), text = "Pause")
        }
    }
}
































