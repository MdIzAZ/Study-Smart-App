package com.example.studysmart.presentation.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.studysmart.list
import com.example.studysmart.presentation.components.DeleteDialog
import com.example.studysmart.presentation.components.PriorityButton
import com.example.studysmart.presentation.components.RelatedToSubjectSection
import com.example.studysmart.presentation.components.SubjectListBottomSheet
import com.example.studysmart.presentation.components.TaskCheckBox
import com.example.studysmart.presentation.components.TaskDatePicker
import com.example.studysmart.util.Priority
import com.example.studysmart.util.toActualDateInString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskScreenNavArgs(
    val subjectId: Int? = null,
    val taskId: Int?,
)


@Destination(navArgsDelegate = TaskScreenNavArgs::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    navigator: DestinationsNavigator
) {

    //viewmodel
    val viewModel = hiltViewModel<TaskViewModel>()

    //states for bottom sheet
    var isBottomSheetOpened by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var selectedSubject by remember { mutableStateOf("English") }

    //states for delete Dialog
    var isDeleteDialogOpened by remember { mutableStateOf(false) }

    //states for two text fields
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    //states for date picker
    var isDatePickerOpened by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )




    DeleteDialog(
        isOpened = isDeleteDialogOpened,
        title = "Delete Task?",
        bodyText = "Are you sure want to delete?",
        onDismissRequest = { isDeleteDialogOpened = false }) {

    }

    TaskDatePicker(
        state = datePickerState,
        isOpened = isDatePickerOpened,
        onConfirmButtonClick = { isDatePickerOpened = true },
        onDismissButtonClick = { isDatePickerOpened = false }
    )

    SubjectListBottomSheet(
        sheetState = bottomSheetState,
        isOpened = isBottomSheetOpened,
        subjects = list,
        onSubjectClicked = {subject->
            scope.launch {bottomSheetState.hide()}.invokeOnCompletion {
                selectedSubject = subject.name
                isBottomSheetOpened = false
            }
        },
        onDismissRequest = { isBottomSheetOpened = false }
    )

    Scaffold(
        topBar = {
            TaskScreenTopAppBar(
                isTaskExists = true,
                isCompleted = false,
                checkboxBorderColor = Color.Red,
                onBackButtonClick = { navigator.navigateUp() },
                onDeleteClick = { isDeleteDialogOpened = true }
            ) {

            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Title") },
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = { description = it },
                label = { Text(text = "Description") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Due Date", style = MaterialTheme.typography.bodySmall)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = datePickerState.selectedDateMillis.toActualDateInString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { isDatePickerOpened = true }) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "date range")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Priority", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        backgroundColor = priority.color,
                        borderColor = if (priority == Priority.MEDIUM) MaterialTheme.colorScheme.onBackground
                        else Color.Transparent,
                        labelColor = if (priority == Priority.MEDIUM) MaterialTheme.colorScheme.primary.copy(
                            alpha = .7f
                        )
                        else Color.Transparent,
                    ) {
//                        onclick
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            RelatedToSubjectSection(selectedSubject = selectedSubject) {
                isBottomSheetOpened = true
            }
            Button(
                onClick = {  },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}











































@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskScreenTopAppBar(
    isTaskExists: Boolean,
    isCompleted: Boolean,
    checkboxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCheckBoxClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = "Task", style = MaterialTheme.typography.headlineSmall) },
        navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigate Back")
            }
        },
        actions = {
            if (isTaskExists) {
                TaskCheckBox(isCompleted = isCompleted, borderColor = checkboxBorderColor) {
                    onCheckBoxClick.invoke()
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                }
            }
        }
    )
}
