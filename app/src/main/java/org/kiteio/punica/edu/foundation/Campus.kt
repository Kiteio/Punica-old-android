package org.kiteio.punica.edu.foundation

import androidx.annotation.StringRes
import org.kiteio.punica.R

/**
 * 校区
 * @property nameResId 名称
 * @property id
 */
sealed class Campus(@StringRes val nameResId: Int, val id: Int) {
    /** 广州 */
    data object Canton : Campus(R.string.campus_canton, 1)

    /** 佛山 */
    data object Foshan : Campus(R.string.campus_foshan, 2)


    companion object {
        /** [Campus] 所有值 */
        val values by lazy { listOf(Canton, Foshan) }


        /**
         * 通过 id 获取 [Campus]
         * @param id
         * @return [Campus]
         */
        fun getById(id: Int?) = when(id) {
            Canton.id, null -> Canton
            Foshan.id -> Foshan
            else -> error("No such a Campus id.")
        }
    }
}