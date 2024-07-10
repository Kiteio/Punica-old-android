package org.kiteio.punica.edu

import androidx.annotation.StringRes
import org.kiteio.punica.R
import org.kiteio.punica.getString

/**
 * 紧急电话
 * @property nameResId 单位
 * @property phoneNumber 电话号码
 * @property workingHours 工作时间
 */
sealed class EmergencyCall(
    @StringRes val nameResId: Int,
    val phoneNumber: String,
    val workingHours: () -> String
) {
    data object CampusAlarm : EmergencyCall(
        R.string.campus_alarm,
        "84096060",
        { getString(R.string.number_of_hours, 24) }
    )

    data object CampusPoliceOffice : EmergencyCall(
        R.string.campus_police_office,
        "84096110",
        { "8:30 - 17:30" }
    )

    data object GuanzhouPoliceOffice : EmergencyCall(
        R.string.guanzhou_police_station,
        "84092782",
        { getString(R.string.number_of_hours, 24) }
    )
}