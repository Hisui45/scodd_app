package com.example.scodd.model

import androidx.compose.runtime.Immutable

@Immutable
data class RoutineInfo(
    val date: Long? = null,
    val hour: Int = 0,
    val minute: Int = 0,
    val isOneTime: Boolean = true,
    val scheduleType: ScoddTime = ScoddTime.DAILY,
    val frequencyValue: Int = 1,
    val frequencyOption: ScoddTime = ScoddTime.DAY,
    val weeklyDay: ScoddTime = ScoddTime.SUNDAY
)

enum class ScoddTime(
    val title: String,
    var selected: Boolean = false
) {
    DAILY("Daily", true),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    YEARLY("Yearly"),
    CUSTOM("Custom"),
    SUNDAY("Sun", true),
    MONDAY("Mon"),
    TUESDAY("Tue"),
    WEDNESDAY("Wed"),
    THURSDAY("Thu"),
    FRIDAY("Fri"),
    SATURDAY("Sat"),
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    YEAR("year"),
    SECOND("second"),
    MINUTE("minute"),
    HOUR("hour")
}

val scoddTimings = listOf(ScoddTime.DAILY, ScoddTime.WEEKLY, ScoddTime.MONTHLY, ScoddTime.YEARLY, ScoddTime.CUSTOM)
val scoddDaysOfWeek = listOf(
    ScoddTime.SUNDAY,
    ScoddTime.MONDAY,
    ScoddTime.TUESDAY,
    ScoddTime.WEDNESDAY,
    ScoddTime.THURSDAY,
    ScoddTime.FRIDAY,
    ScoddTime.SATURDAY
)
val scoddFrequency = listOf(ScoddTime.DAY, ScoddTime.WEEK, ScoddTime.MONTH, ScoddTime.YEAR)
val scoddTimeUnits = listOf(ScoddTime.SECOND, ScoddTime.MINUTE, ScoddTime.HOUR)
