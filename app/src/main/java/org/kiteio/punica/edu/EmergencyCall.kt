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
    /** 广州校区 */
    sealed class Canton(
        @StringRes nameResId: Int,
        phoneNumber: String,
        workingHours: () -> String
    ) : EmergencyCall(nameResId, phoneNumber, workingHours) {
        /** 门诊部 */
        data object Clinic : EmergencyCall(
            R.string.clinic,
            "13112234297",
            { getString(R.string.number_of_hours, 24) }
        )

        /** 校园报警 */
        data object CampusAlarm : Canton(
            R.string.campus_alarm,
            "020-84096060",
            { getString(R.string.number_of_hours, 24) }
        )

        /** 校园警务室 */
        data object CampusPoliceOffice : Canton(
            R.string.campus_police_office,
            "020-84096110",
            { "8:30 - 17:30" }
        )

        /** 官洲派出所 */
        data object GuanzhouPoliceOffice : Canton(
            R.string.guanzhou_police_station,
            "020-84092782",
            { getString(R.string.number_of_hours, 24) }
        )

        companion object {
            val values by lazy { listOf(Clinic, CampusAlarm, CampusPoliceOffice, GuanzhouPoliceOffice) }
        }
    }


    /** 佛山校区 */
    sealed class Foshan(
        @StringRes nameResId: Int,
        phoneNumber: String,
        workingHours: () -> String
    ) : EmergencyCall(nameResId, phoneNumber, workingHours) {
        /** 门诊部 */
        data object Clinic : Foshan(
            R.string.clinic,
            "18566890063",
            { getString(R.string.number_of_hours, 24) }
        )

        /** 校园报警 */
        data object CampusAlarm : Foshan(
            R.string.campus_alarm,
            "0757-87828110",
            { getString(R.string.number_of_hours, 24) }
        )

        companion object {
            val values by lazy { listOf(Clinic, CampusAlarm) }
        }
    }
}