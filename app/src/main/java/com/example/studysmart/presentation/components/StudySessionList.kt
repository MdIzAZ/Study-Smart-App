package com.example.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studysmart.R
import com.example.studysmart.domain.models.Session


fun LazyListScope.studySessionList(
    sectionTitle: String,
    sessions: List<Session>,
    emptyListText: String,
    onDeleteIconClick: (Session) -> Unit
) {
    item {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )
    }
    if (sessions.isEmpty()) {
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    painter = painterResource(id = R.drawable.img_lamp),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
    items(sessions) {
        SessionCard(
            session = it,
            modifier = Modifier.padding(12.dp, 4.dp),
            onDeleteIconClick = {
                onDeleteIconClick(it)
            }
        )
    }
}