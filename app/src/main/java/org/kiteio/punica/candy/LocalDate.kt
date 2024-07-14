package org.kiteio.punica.candy

import java.time.LocalDate

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