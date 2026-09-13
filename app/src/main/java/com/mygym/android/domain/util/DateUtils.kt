package com.mygym.android.domain.util

import com.mygym.android.domain.model.DayProgress
import com.mygym.android.domain.model.WeekProgress
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val PT_BR = Locale("pt", "BR")

    fun getToday(): LocalDate = LocalDate.now()

    fun formatMonthYear(date: LocalDate): String {
        val month = date.month.getDisplayName(TextStyle.FULL, PT_BR)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(PT_BR) else it.toString() }
        return "$month ${date.year}"
    }

    fun formatHeaderDate(date: LocalDate): String {
        val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.FULL, PT_BR)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(PT_BR) else it.toString() }
        val month = date.month.getDisplayName(TextStyle.FULL, PT_BR)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(PT_BR) else it.toString() }
        return "$dayOfWeek, ${date.dayOfMonth} De $month"
    }

    fun getDayOfWeekLetter(dayOfWeek: DayOfWeek): String {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> "D"
            DayOfWeek.MONDAY -> "S"
            DayOfWeek.TUESDAY -> "T"
            DayOfWeek.WEDNESDAY -> "Q"
            DayOfWeek.THURSDAY -> "Q"
            DayOfWeek.FRIDAY -> "S"
            DayOfWeek.SATURDAY -> "S"
        }
    }

    /**
     * Retorna os 7 dias da semana para a data fornecida.
     * Iniciando no Domingo (ou Segunda-feira). No Brasil e no mockup, o calendário exibe D, S, T, Q, Q, S, S (iniciando Domingo).
     */
    fun getWeekDays(referenceDate: LocalDate): List<LocalDate> {
        // Encontra o domingo anterior ou atual
        val daysFromSunday = referenceDate.dayOfWeek.value % 7
        val sunday = referenceDate.minusDays(daysFromSunday.toLong())
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    /**
     * Constrói o objeto WeekProgress baseado nas datas completadas e no dia de referência.
     */
    fun buildWeekProgress(
        referenceDate: LocalDate = getToday(),
        completedDates: Set<LocalDate>,
        scheduledDaysOfWeek: Set<DayOfWeek> = emptySet(),
        weeklyGoalTotal: Int = 5
    ): WeekProgress {
        val weekDays = getWeekDays(referenceDate)
        val today = getToday()

        val dayProgresses = weekDays.map { date ->
            DayProgress(
                date = date,
                dayOfWeekLetter = getDayOfWeekLetter(date.dayOfWeek),
                dayNumber = date.dayOfMonth,
                isToday = date == today,
                hasCompletedWorkout = completedDates.contains(date),
                hasScheduledWorkout = scheduledDaysOfWeek.contains(date.dayOfWeek)
            )
        }

        val completedThisWeek = weekDays.count { completedDates.contains(it) }
        val activeStreak = calculateStreak(today, completedDates)

        return WeekProgress(
            monthYearText = formatMonthYear(referenceDate),
            days = dayProgresses,
            activeStreakDays = activeStreak,
            weeklyGoalCompleted = completedThisWeek,
            weeklyGoalTotal = weeklyGoalTotal
        )
    }

    /**
     * Calcula a sequência ativa de dias com treinos realizados até a data atual.
     */
    fun calculateStreak(today: LocalDate, completedDates: Set<LocalDate>): Int {
        if (completedDates.isEmpty()) return 0

        var streak = 0
        var checkDate = today

        // Se hoje ainda não treinou, o streak pode estar ativo até ontem
        if (!completedDates.contains(checkDate)) {
            checkDate = checkDate.minusDays(1)
        }

        while (completedDates.contains(checkDate)) {
            streak++
            checkDate = checkDate.minusDays(1)
        }

        return streak
    }
}

