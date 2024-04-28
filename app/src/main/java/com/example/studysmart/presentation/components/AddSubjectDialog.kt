package com.example.studysmart.presentation.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.studysmart.domain.models.Subject

@Composable
fun AddSubjectDialog(
    isOpened: Boolean,
    title: String,
    subjectName: String,
    goalHour: String,
    onSubjectNameChange: (String) -> Unit,
    onGoalHourChange: (String) -> Unit,
    selectedColor: List<Color>,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onSelectedColorChange: (List<Color>) -> Unit,
) {

    val subjectNameError = rememberSaveable { mutableStateOf<String?>(null) }
    val goalHourError = rememberSaveable { mutableStateOf<String?>(null) }

    subjectNameError.value = when {
        subjectName.isBlank() -> "Please enter subject name"
        subjectName.length < 2 -> "Subject name is too short"
        subjectName.length > 20 -> "Subject name is too long"
        else -> null
    }

    goalHourError.value = when {
        goalHour.isBlank() -> "Please enter goal hour"
        goalHour.toFloatOrNull() == null -> "Invalid number"
        goalHour.toFloat() < 1f -> "Set at least one hour"
        goalHour.toFloat() > 1000f -> "max: 1000 hr possible"
        else -> null
    }


    if (isOpened) {
        AlertDialog(
            title = { Text(text = title) },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Subject.colors.forEach {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(brush = Brush.verticalGradient(it))
                                    .border(
                                        width = 1.dp,
                                        color = if (it == selectedColor)
                                            MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { onSelectedColorChange(it) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = subjectName,
                        onValueChange = onSubjectNameChange,
                        label = { Text(text = "Subject Name") },
                        isError = subjectName.isNotBlank() && subjectNameError.value != null,
                        supportingText = { Text(text = subjectNameError.value.orEmpty()) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = goalHour,
                        onValueChange = onGoalHourChange,
                        label = { Text(text = "Goal Hour") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = goalHour.isNotBlank() && goalHourError.value != null,
                        supportingText = { Text(text = goalHourError.value.orEmpty()) }
                    )
                }
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                    enabled = subjectNameError.value == null && goalHourError.value == null
                ) {
                    Text(text = "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = "Cancel")
                }
            }
        )
    }

}