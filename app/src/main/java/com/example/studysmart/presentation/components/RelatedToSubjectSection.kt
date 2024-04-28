package com.example.studysmart.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun RelatedToSubjectSection(
    selectedSubject: String,
    onDropDownButtonClick: ()->Unit
){
    Text(text = "Related to Subject", style = MaterialTheme.typography.bodyLarge)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = selectedSubject, style = MaterialTheme.typography.bodyLarge)
        IconButton(onClick = { onDropDownButtonClick() }) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "select subject"
            )
        }
    }
}