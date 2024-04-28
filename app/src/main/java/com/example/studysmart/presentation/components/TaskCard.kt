package com.example.studysmart.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.util.Priority

@Composable
fun TaskCard(
    modifier: Modifier = Modifier,
    tasks: Tasks,
    onCheckBoxClick: ()->Unit,
    onClick: ()->Unit
) {
    ElevatedCard(
        modifier = modifier.clickable { onClick.invoke() }
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            TaskCheckBox(
                isCompleted = tasks.isCompleted,
                borderColor = Priority.fromInt(tasks.priority).color,
                onCheckBoxClick
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = tasks.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (tasks.isCompleted) {
                        TextDecoration.LineThrough
                    } else TextDecoration.None
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${tasks.dueDate}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}