package org.kiteio.punica.edu.system.api.course

import kotlinx.serialization.Serializable

/**
 * 课程分类
 * @property listRoute
 * @property selectRoute
 */
@Serializable
sealed class Sort {
    abstract val listRoute: String
    abstract val selectRoute: String

    /** 必修课 */
    @Serializable
    data object Basic : Unsearchable("Bx", "bx")

    /** 选修课 */
    @Serializable
    data object Optional : Unsearchable("Xx", "xx")

    /** 通识课 */
    @Serializable
    data object General : Searchable("Ggxxk", "ggxxk")

    /** 跨专业 */
    @Serializable
    data object CrossMajor : Searchable("Faw", "faw")

    /** 跨年级 */
    @Serializable
    data object CrossYear : Searchable("Knj", "knj")

    /** 专业内计划 */
    @Serializable
    data object Major : Searchable("Bxqjh", "bxqjh")


    /** 可搜索课程 */
    @Serializable
    sealed class Searchable(override val listRoute: String, override val selectRoute: String) : Sort()


    /** 不可搜索课程 */
    @Serializable
    sealed class Unsearchable(override val listRoute: String, override val selectRoute: String) : Sort()
}