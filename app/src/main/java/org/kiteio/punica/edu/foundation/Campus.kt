package org.kiteio.punica.edu.foundation

import androidx.annotation.StringRes
import org.kiteio.punica.R

sealed class Campus(@StringRes val resId: Int, val id: String) {
    data object Guangzhou : Campus(R.string.campus_guangzhou, "1")
    data object Foshan : Campus(R.string.campus_foshan, "2")
}