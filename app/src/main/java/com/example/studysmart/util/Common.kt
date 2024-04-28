package com.example.studysmart.util

import androidx.compose.ui.graphics.Color
import com.example.studysmart.presentation.theme.Green
import com.example.studysmart.presentation.theme.Orange
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class Priority(val title: String, val color: Color, val value: Int) {
    LOW("Low", Green, 0),
    MEDIUM("Medium", Orange, 1),
    HIGH("High", Color.Red, 2);

    companion object {
        fun fromInt(value: Int) =
            entries.firstOrNull() { it.value == value } ?: MEDIUM
    }
}

fun Long?.toActualDateInString(): String {
    val date: LocalDate = this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } ?: LocalDate.now()
    return date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
}

fun Long.toHours(): Float {
    val hour = this.toFloat() / 3600f
    return  "%.2f".format(hour).toFloat()
}