package org.kiteio.punica.edu

import androidx.annotation.StringRes
import org.kiteio.punica.AppContext
import org.kiteio.punica.R

/**
 * 紧急电话
 * @property name 单位
 * @property phoneNumber 电话号码
 * @property workingHours 工作时间
 * @constructor
 */
sealed class EmergencyCall(
    @StringRes val name: Int,
    val phoneNumber: String,
    val workingHours: () -> String
) {
    data object CampusAlarm : EmergencyCall(
        R.string.campus_alarm,
        "84096060",
        { AppContext.getString(R.string.number_of_hours, 24) }
    )

    data object CampusPoliceOffice : EmergencyCall(
        R.string.campus_police_office,
        "84096110",
        { "8:30 - 17:30" }
    )

    data object GuanzhouPoliceOffice : EmergencyCall(
        R.string.guanzhou_police_station,
        "84092782",
        { AppContext.getString(R.string.number_of_hours, 24) }
    )
}