package org.kiteio.punica.edu.foundation

import androidx.annotation.IntRange
import kotlinx.serialization.Serializable
import java.time.LocalDate

/**
 * 学期
 * @property year 开始年份
 * @property term 学期
 */
@Serializable
data class Semester(
    @IntRange(from = 1983) val year: Int,
    @IntRange(from = 1, to = 2) val term: Int
) {
    override fun toString() = "$year-${year + 1}-$term"


    operator fun plus(other: Int) = offset(other) { it }


    operator fun minus(other: Int) = offset(other) { -it - 1 }


    private fun offset(offset: Int, cal: (Int) -> Int) = (term - 1).let {  // 将 term 标准化为 0 或 1
        Semester(year + (it + cal(offset)) / 2, (it + offset) % 2 + 1)
    }


    companion object {
        /**
         * 将 [localDate] 转化为 [Semester]
         * @param localDate
         * @return [Semester]
         */
        fun of(localDate: LocalDate) = with(localDate) {
            when (month.ordinal) {
                1 -> Semester(year - 1, 1)
                in 2..8 -> Semester(year - 1, 2)
                else -> Semester(year, 1)
            }
        }


        /**
         * 将 [string] 转化为 [Semester]
         * @param string 如 2024-2025-1
         * @return [Semester]
         */
        fun of(string: String) = string.split("-").run {
            Semester(get(0).toInt(), get(2).toInt())
        }


        /**
         * 返回 [username] 大学各个学期
         * @param username
         * @return [List]<[Semester]>
         */
        fun listFor(username: String): List<Semester> = with(enrolledOf(username)) {
            mutableListOf<Semester>().apply {
                for (i in 0..7) add(this@with + i)
            }
        }


        /**
         * 获取 [username] 的入学学期
         * @param username 学号
         * @return [Semester]
         */
        private fun enrolledOf(username: String) =
            Semester(("20" + username.substring(0..1)).toInt(), 1)
    }
}