package com.maaly.life.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(viewModel: CalendarViewModel = viewModel()) {
    val mode by viewModel.mode.collectAsState()
    val referenceDate by viewModel.referenceDate.collectAsState()
    val dayStatuses by viewModel.dayStatuses.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "التقويم", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        ModeSelector(mode = mode, onModeChange = { viewModel.setMode(it) })
        Spacer(modifier = Modifier.height(12.dp))

        PeriodHeader(
            mode = mode,
            referenceDate = referenceDate,
            onPrevious = { viewModel.goPrevious() },
            onNext = { viewModel.goNext() }
        )
        Spacer(modifier = Modifier.height(16.dp))

        when (mode) {
            CalendarMode.DAY -> DayDetailView(dayStatuses.firstOrNull())
            CalendarMode.WEEK -> DayGrid(dayStatuses, columns = 7)
            CalendarMode.MONTH -> DayGrid(dayStatuses, columns = 7)
            CalendarMode.YEAR -> YearHeatmap(dayStatuses)
        }
    }
}

@Composable
private fun ModeSelector(mode: CalendarMode, onModeChange: (CalendarMode) -> Unit) {
    val labels = mapOf(
        CalendarMode.DAY to "يومي",
        CalendarMode.WEEK to "أسبوعي",
        CalendarMode.MONTH to "شهري",
        CalendarMode.YEAR to "سنوي"
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        labels.forEach { (m, label) ->
            val selected = m == mode
            FilterChip(
                selected = selected,
                onClick = { onModeChange(m) },
                label = { Text(label) },
                modifier = Modifier.padding(end = 6.dp)
            )
        }
    }
}

@Composable
private fun PeriodHeader(
    mode: CalendarMode,
    referenceDate: Calendar,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    val label = when (mode) {
        CalendarMode.DAY -> SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar")).format(referenceDate.time)
        CalendarMode.WEEK -> "الأسبوع — " + SimpleDateFormat("d MMMM", Locale("ar")).format(referenceDate.time)
        CalendarMode.MONTH -> SimpleDateFormat("MMMM yyyy", Locale("ar")).format(referenceDate.time)
        CalendarMode.YEAR -> SimpleDateFormat("yyyy", Locale("ar")).format(referenceDate.time)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "السابق")
        }
        Text(text = label, style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onNext) {
            Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "التالي")
        }
    }
}

@Composable
private fun DayGrid(days: List<DayStatus>, columns: Int) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { day -> DayCell(day) }
    }
}

@Composable
private fun DayCell(day: DayStatus) {
    val dayNumber = day.date.substringAfterLast("-").toIntOrNull()?.toString() ?: "-"
    val bgColor = ratioColor(day.ratio)

    Column(
        modifier = Modifier
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = dayNumber, textAlign = TextAlign.Center)
        if (day.totalTasks > 0) {
            Text(
                text = "${day.completedTasks}/${day.totalTasks}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun YearHeatmap(days: List<DayStatus>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(12),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(days) { day ->
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .size(14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ratioColor(day.ratio))
            )
        }
    }
}

@Composable
private fun DayDetailView(day: DayStatus?) {
    if (day == null) {
        Text(text = "لا توجد بيانات")
        return
    }
    Column {
        Text(text = "المهام: ${day.completedTasks} من ${day.totalTasks}")
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(day.ratio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
                    .background(ratioColor(day.ratio))
            )
        }
    }
}

private fun ratioColor(ratio: Float): Color {
    return when {
        ratio == 0f -> Color(0xFFE0E0E0)
        ratio < 0.5f -> Color(0xFFFFCC80)
        ratio < 1f -> Color(0xFF90CAF9)
        else -> Color(0xFF81C784)
    }
}
