package org.kiteio.punica.candy

import java.time.LocalDate

/** 日期对应的学期 */
val LocalDate.semester
    get() = run {
        when (month.ordinal) {
            1 -> "${year - 1}-$year-1"
            in 2..8 -> "${year - 1}-$year-2"
            else -> "$year-${year + 1}-1"
        }
    }

/** 时间戳 */
val LocalDate.dateMillis
    get() = toEpochDay() * 24 * 60 * 60 * 1000L


/**
 * [LocalDate]
 * @param dateMillis
 * @return [LocalDate]
 */
fun LocalDate(dateMillis: Long): LocalDate =
    LocalDate.ofEpochDay(dateMillis / (24 * 60 * 60 * 1000L))