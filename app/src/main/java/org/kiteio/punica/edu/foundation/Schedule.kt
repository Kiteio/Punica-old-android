package org.kiteio.punica.edu.foundation

import java.time.LocalTime

private typealias Section = Pair<LocalTime, LocalTime>

/**
 * 时间表
 * @property campus
 * @property items
 */
data class Schedule(
    val campus: Campus,
    val items: List<Pair<Section, Section>>
) {
    companion object {
        /** 广州 */
        private val Canton = Schedule(
            Campus.Canton,
            generateSchedule(
                start = LocalTime.of(8, 0),
                smallRestMin = 10L,
                largeRestMin = { if (it == 4) 10L else 20L },
                lunchRestMin = 150L,
                dinnerRestMin = 50L
            )
        )

        /** 佛山 */
        private val FoShan = Schedule(
            Campus.Foshan,
            generateSchedule(
                start =  LocalTime.of(8, 30),
                smallRestMin = 0,
                largeRestMin = { 20L },
                lunchRestMin = 130L,
                dinnerRestMin = 70L
            )
        )


        /**
         * 通过 id 获取 [Schedule]
         * @param id
         * @return [Schedule]
         */
        fun getById(id: Int?) = when(id) {
            Campus.Canton.id, null -> Canton
            Campus.Foshan.id -> FoShan
            else -> error("No such a Campus id.")
        }
    }
}


/**
 * 时间表生成
 * @param start 开始时间
 * @param smallRestMin 小课间分钟数
 * @param largeRestMin 大课间分钟数
 * @param lunchRestMin 午餐时间分钟数
 * @param dinnerRestMin 晚餐时间分钟数
 * @return [List]<[Pair]<[Section], [Section]>>
 */
fun generateSchedule(
    start: LocalTime,
    smallRestMin: Long,
    largeRestMin: (Int) -> Long,
    lunchRestMin: Long,
    dinnerRestMin: Long
): List<Pair<Section, Section>> {
    val classMin = 45L

    val items = mutableListOf<Pair<Section, Section>>()
    var time = start
    for (part in 0..5) {
        // 一节课（包含小课间）
        val first = time to time.plusMinutes(classMin).also {
            time = it.plusMinutes(smallRestMin)
        }
        val second = time to time.plusMinutes(classMin).also { time = it }
        items.add(first to second)

        if (part == 5) break

        // 大课间
        val minutes = when (part) {
            1 -> lunchRestMin
            3 -> dinnerRestMin
            else -> largeRestMin(part)
        }
        time = time.plusMinutes(minutes)
    }

    return items
}