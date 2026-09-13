package com.mygym.android.domain

import com.mygym.android.domain.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun testGetWeekDays_returns7DaysStartingSunday() {
        // 2026-09-12 is Saturday
        val saturday = LocalDate.of(2026, 9, 12)
        val weekDays = DateUtils.getWeekDays(saturday)

        assertEquals(7, weekDays.size)
        // Sunday should be 2026-09-06
        assertEquals(LocalDate.of(2026, 9, 6), weekDays[0])
        assertEquals(DayOfWeek.SUNDAY, weekDays[0].dayOfWeek)
        // Saturday should be 2026-09-12
        assertEquals(saturday, weekDays[6])
        assertEquals(DayOfWeek.SATURDAY, weekDays[6].dayOfWeek)
    }

    @Test
    fun testCalculateStreak_empty_returns0() {
        val today = LocalDate.of(2026, 9, 12)
        val streak = DateUtils.calculateStreak(today, emptySet())
        assertEquals(0, streak)
    }

    @Test
    fun testCalculateStreak_trainedToday_returnsCorrectStreak() {
        val today = LocalDate.of(2026, 9, 12)
        val completed = setOf(
            LocalDate.of(2026, 9, 10),
            LocalDate.of(2026, 9, 11),
            LocalDate.of(2026, 9, 12)
        )
        val streak = DateUtils.calculateStreak(today, completed)
        assertEquals(3, streak)
    }

    @Test
    fun testCalculateStreak_trainedYesterdayButNotToday_maintainsStreak() {
        val today = LocalDate.of(2026, 9, 12)
        val completed = setOf(
            LocalDate.of(2026, 9, 10),
            LocalDate.of(2026, 9, 11)
        )
        val streak = DateUtils.calculateStreak(today, completed)
        assertEquals(2, streak)
    }

    @Test
    fun testCalculateStreak_brokenStreak_onlyCountsRecentConsecutive() {
        val today = LocalDate.of(2026, 9, 12)
        val completed = setOf(
            LocalDate.of(2026, 9, 5), // gap
            LocalDate.of(2026, 9, 11),
            LocalDate.of(2026, 9, 12)
        )
        val streak = DateUtils.calculateStreak(today, completed)
        assertEquals(2, streak)
    }

    @Test
    fun testBuildWeekProgress_correctlyCalculatesCompletedAndActiveStreak() {
        val today = LocalDate.of(2026, 9, 12)
        val completed = setOf(
            LocalDate.of(2026, 9, 11),
            LocalDate.of(2026, 9, 12)
        )
        val progress = DateUtils.buildWeekProgress(
            referenceDate = today,
            completedDates = completed,
            scheduledDaysOfWeek = setOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
            weeklyGoalTotal = 4
        )

        assertEquals(2, progress.activeStreakDays)
        assertEquals(2, progress.weeklyGoalCompleted)
        assertEquals(4, progress.weeklyGoalTotal)
        assertEquals(7, progress.days.size)

        val saturdayProgress = progress.days.find { it.date == today }
        assertTrue(saturdayProgress != null && saturdayProgress.hasCompletedWorkout)
        assertTrue(saturdayProgress != null && saturdayProgress.hasScheduledWorkout)
    }
}

