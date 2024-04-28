package com.example.studysmart

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.example.studysmart.domain.models.Session
import com.example.studysmart.domain.models.Subject
import com.example.studysmart.domain.models.Tasks
import com.example.studysmart.presentation.NavGraphs
import com.example.studysmart.presentation.theme.StudySmartTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)
        setContent {
            StudySmartTheme {
                DestinationsNavHost(navGraph = NavGraphs.root)
            }
        }
    }
}

val list = listOf(
    Subject(name = "Physics", goalHour = 10f, colors = Subject.colors[0].map { it.toArgb() }),
    Subject(name = "Computer", goalHour = 9f, colors = Subject.colors[1].map { it.toArgb() }),
    Subject(name = "Math", goalHour = 7f, colors = Subject.colors[2].map { it.toArgb() }),
    Subject(name = "Chemistry", goalHour = 7f, colors = Subject.colors[3].map { it.toArgb() }),
    Subject(name = "Automata", goalHour = 7f, colors = Subject.colors[4].map { it.toArgb() }),
)
val tasks = listOf(
    Tasks("Prepare Notes", "", 0f, 0, "", false, 5)
)
val sessionList = listOf(
    Session(1, "Physics", 6L, 7L, 2),
    Session(1, "Physics", 6L, 7L, 2),
    Session(1, "Physics", 6L, 7L, 2),
    Session(1, "Physics", 6L, 7L, 2),
    Session(1, "Physics", 6L, 7L, 2),
    Session(1, "Physics", 6L, 7L, 2),
)


