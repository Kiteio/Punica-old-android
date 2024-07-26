package org.kiteio.punica.edu.system.api.course

/**
 * 课程分类
 * @property listRoute
 * @property selectRoute
 */
sealed class Sort(val listRoute: String, val selectRoute: String) {
    /** 必修课 */
    data object Basic : Unsearchable("Bx", "bx")

    /** 选修课 */
    data object Optional : Unsearchable("Xx", "xx")

    /** 通识课 */
    data object General : Searchable("Ggxxk", "ggxxk")

    /** 跨专业 */
    data object CrossMajor : Searchable("Faw", "faw")

    /** 跨年级 */
    data object CrossYear : Searchable("Knj", "knj")

    /** 专业内计划 */
    data object Major : Searchable("Bxqjh", "bxqjh")


    /** 可搜索课程 */
    sealed class Searchable(listRoute: String, selectRoute: String) :
        Sort(listRoute, selectRoute)


    /** 不可搜索课程 */
    sealed class Unsearchable(listRoute: String, selectRoute: String) :
        Sort(listRoute, selectRoute)
}