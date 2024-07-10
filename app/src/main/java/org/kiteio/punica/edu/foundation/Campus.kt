package org.kiteio.punica.edu.foundation

import androidx.annotation.StringRes
import org.kiteio.punica.R

/**
 * 校区
 * @property nameResId 名称
 * @property id
 */
sealed class Campus(@StringRes val nameResId: Int, val id: Int) {
    data object Guangzhou : Campus(R.string.campus_guangzhou, 1)
    data object Foshan : Campus(R.string.campus_foshan, 2)


    companion object {
        /** 列表 */
        val list by lazy { listOf(Guangzhou, Foshan) }


        /**
         * 通过 id 获取 [Campus]
         * @param id
         * @return [Campus]
         */
        fun getById(id: Int) = when(id) {
            Guangzhou.id -> Guangzhou
            Foshan.id -> Foshan
            else -> error("No such a Campus id.")
        }
    }
}