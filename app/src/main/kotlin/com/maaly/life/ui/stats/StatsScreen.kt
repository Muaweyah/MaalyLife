package com.maaly.life.ui.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maaly.life.ui.rewards.RewardsCalculator
import kotlin.math.roundToInt

@Composable
fun StatsScreen(viewModel: StatsViewModel = viewModel()) {
    val period by viewModel.period.collectAsState()
    val stats by viewModel.categoryStats.collectAsState()
    val overallRatio by viewModel.overallRatio.collectAsState()
    val streak by viewModel.streak.collectAsState()

    val totalCompleted = stats.sumOf { it.completed }
    val points = RewardsCalculator.pointsFor(totalCompleted)
    val badge = RewardsCalculator.badgeFor(streak)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "الإحصائيات", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        if (streak > 0) {
            StreakBanner(streak)
            Spacer(modifier = Modifier.height(12.dp))
        }

        PointsAndBadgeCard(points, badge)
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            FilterChip(
                selected = period == StatsPeriod.WEEK,
                onClick = { viewModel.setPeriod(StatsPeriod.WEEK) },
                label = { Text("أسبوعي") },
                modifier = Modifier.padding(end = 6.dp)
            )
            FilterChip(
                selected = period == StatsPeriod.MONTH,
                onClick = { viewModel.setPeriod(StatsPeriod.MONTH) },
                label = { Text("شهري") }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        OverallCard(overallRatio)

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "نسبة الإنجاز حسب التصنيف", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (stats.isEmpty()) {
            Text(text = "لا توجد بيانات لهذه الفترة بعد")
        } else {
            stats.forEach { stat -> CategoryRow(stat) }
        }
    }
}

@Composable
private fun PointsAndBadgeCard(points: Int, badge: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp)
    ) {
        Text(text = "🌟 $points نقطة")
        if (badge != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = badge)
        }
    }
}

@Composable
private fun StreakBanner(streak: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFFF3E0))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "🔥", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "$streak يوم متتالي من إنجاز كل المهام")
    }
}

@Composable
private fun OverallCard(ratio: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F3F3))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(ratioColor(ratio)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${(ratio * 100).roundToInt()}%",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = "نسبة الإنجاز الإجمالية لهذه الفترة")
    }
}

@Composable
private fun CategoryRow(stat: CategoryStat) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stat.category)
            Text(text = "${stat.completed}/${stat.total} — ${(stat.ratio * 100).roundToInt()}%")
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE0E0E0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(stat.ratio)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(ratioColor(stat.ratio))
            )
        }
    }
}

private fun ratioColor(ratio: Float): Color {
    return when {
        ratio == 0f -> Color(0xFFBDBDBD)
        ratio < 0.5f -> Color(0xFFFFA726)
        ratio < 1f -> Color(0xFF42A5F5)
        else -> Color(0xFF66BB6A)
    }
}
