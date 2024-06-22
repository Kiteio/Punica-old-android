package org.kiteio.punica.candy

import java.time.LocalDate

/**
 * 日期对应的学期
 */
val LocalDate.semester get() = run {
    when (month.ordinal) {
        1 -> "${year - 1}-$year-1"
        in 2..8 -> "${year - 1}-$year-2"
        else -> "$year-${year + 1}-1"
    }
}