package org.kiteio.punica.edu.system.api.course

import androidx.annotation.StringRes
import org.kiteio.punica.R

/**
 * 课程分类
 * @property resId
 * @property listRoute
 * @property selectRoute
 * @constructor
 */
sealed class Sort(@StringRes val resId: Int, val listRoute: String, val selectRoute: String) {
    data object Basic : Unsearchable(R.string.course_basic, "Bx", "bx")
    data object Optional : Unsearchable(R.string.course_optional, "Xx", "xx")

    data object General : Searchable(R.string.course_general, "Ggxxk", "ggxxk")
    data object CrossYear : Searchable(R.string.course_cross_year, "Knj", "knj")
    data object CrossMajor : Searchable(R.string.course_cross_major, "Faw", "faw")
    data object Major : Searchable(R.string.course_major, "Bxqjh", "bxqjh")
}


/**
 * 可搜索课程
 * @constructor
 */
sealed class Searchable(@StringRes resId: Int, listRoute: String, selectRoute: String) :
    Sort(resId, listRoute, selectRoute)


/**
 * 不可搜索课程
 * @constructor
 */
sealed class Unsearchable(@StringRes resId: Int, listRoute: String, selectRoute: String) :
    Sort(resId, listRoute, selectRoute)