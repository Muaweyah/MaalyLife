package com.maaly.life.ui.rewards

object RewardsCalculator {
    fun pointsFor(completedTasks: Int): Int = completedTasks * 10

    fun badgeFor(streak: Int): String? {
        return when {
            streak >= 30 -> "🏆 شارة الالتزام الذهبية (30 يوم)"
            streak >= 14 -> "🥈 شارة أسبوعين متواصلين"
            streak >= 7 -> "🥉 شارة أسبوع كامل"
            streak >= 3 -> "⭐ شارة البداية القوية"
            else -> null
        }
    }
}
