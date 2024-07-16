package org.kiteio.punica.candy

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

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


/**
 * 返回两个 [LocalDate] 相差的天数
 * @receiver [LocalDate]
 * @param other
 * @return [Long]
 */
fun LocalDate.daysUntil(other: LocalDate) =
    ChronoUnit.DAYS.between(this, other)


/**
 * 返回两个 [LocalDateTime] 相差的分钟数
 * @receiver [LocalDateTime]
 * @param other
 * @return [Long]
 */
fun LocalDateTime.minutesUntil(other: LocalDateTime) =
    ChronoUnit.MINUTES.between(this, other)