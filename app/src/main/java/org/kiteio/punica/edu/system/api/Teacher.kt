package org.kiteio.punica.edu.system.api

import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.parameters
import org.kiteio.punica.edu.system.EduSystem

/**
 * 教师列表
 * @receiver [EduSystem]
 * @param name 教师名
 * @param pageIndex 页码
 * @return [TeacherList]
 */
suspend fun EduSystem.teacherList(name: String, pageIndex: Int = 0): TeacherList {
    val text = session.post(
        route { TEACHER_LIST },
        parameters {
            append("jsxm", name)
            append("pageIndex", if (pageIndex == 0) "" else (pageIndex + 1).toString())
        }
    ).bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementById("Form1")!!
    val rows = table.getElementsByTag("tr")

    // 总页数
    val totalPages = Regex("\\d+").find(
        table.getElementsByClass("Nsb_r_list_fy3").text()
    )!!.value.toInt()

    val items = arrayListOf<TeacherItem>()
    for (index in 1..<rows.size) {
        val infos = rows[index].children()
        items.add(
            TeacherItem(
                id = infos[1].text(),
                name = infos[2].text(),
                department = infos[3].text()
            )
        )
    }

    return TeacherList(totalPages, items)
}


/**
 * 教师列表
 * @property totalPages 总页数
 * @property items
 */
data class TeacherList(
    val totalPages: Int,
    val items: List<TeacherItem>
)


/**
 * 教师列表项
 * @property id 工号
 * @property name 姓名
 * @property department 部门
 */
data class TeacherItem(
    val id: String,
    val name: String,
    val department: String
)


/**
 * 教师信息
 * @receiver [EduSystem]
 * @param id 工号
 * @return [Teacher]
 */
suspend fun EduSystem.teacher(id: String): Teacher {
    val text = session.fetch(route { TEACHER }) {
        parameter("jg0101id", id)
    }.bodyAsText()

    val document = Ksoup.parse(text)
    val table = document.getElementsByClass("no_border_table")[0].child(0)
    val rows = table.children()

    // 是否有联系方式
    val containContact = rows.size == 19

    // 姓名、性别
    val nameGender = rows[1].getElementsByTag("td")
    // 政治面貌、民族
    val politicsNative = rows[2].getElementsByTag("td")
    // 职务、职称
    val dutyTitle = rows[3].getElementsByTag("td")
    // 教职工类别、部门（院系）
    val categoryDepartment = rows[4].getElementsByTag("td")
    // 科室（系）、最高学历
    val officeQualifications = rows[5].getElementsByTag("td")
    // 学位、研究方向
    val degreeField = rows[6].getElementsByTag("td")

    // 获取授课项
    val getCourseItems: (Int) -> List<CourseItem> = { index ->
        val elements = rows[index].getElementsByTag("tbody")[0].getElementsByTag("tr")

        val items = arrayListOf<CourseItem>()
        for (elementIndex in 1..<elements.size) {
            val infos = elements[elementIndex].getElementsByTag("td")
            if (infos.size == 4) items.add(
                CourseItem(
                    name = infos[1].text(),
                    sort = infos[2].text(),
                    semester = infos[3].text()
                )
            )
        }

        items
    }

    // 获取联系方式
    val getContact: (Int, Int) -> String = { rowIndex, contactIndex ->
        if (containContact) rows[rowIndex].getElementsByTag("td")[contactIndex].text()
        else ""
    }

    return Teacher(
        name = nameGender[2].text(),
        gender = nameGender[4].text(),
        politics = politicsNative[1].text(),
        nation = politicsNative[3].text(),
        duty = dutyTitle[1].text(),
        title = dutyTitle[3].text(),
        category = categoryDepartment[1].text(),
        department = categoryDepartment[3].text(),
        office = officeQualifications[1].text(),
        qualifications = officeQualifications[3].text(),
        degree = degreeField[2].text(),
        field = degreeField[4].text(),
        phoneNumber = getContact(7, 2),
        qQ = getContact(7, 4),
        weChat = getContact(8, 2),
        email = getContact(8, 4),
        introduction = rows[if (containContact) 10 else 9].text(),
        taught = getCourseItems(if (containContact) 12 else 11),
        teaching = getCourseItems(if (containContact) 14 else 13),
        philosophy = rows[if (containContact) 16 else 15].text(),
        slogan = rows[if (containContact) 18 else 17].text()
    )
}


/**
 * 教师信息
 * @property name 姓名
 * @property gender 性别
 * @property politics 政治面貌
 * @property nation 民族
 * @property duty 职务
 * @property title 职称
 * @property category 教职工类别
 * @property department 部门（院系）
 * @property office 科室（系）
 * @property qualifications 最高学历
 * @property degree 学位
 * @property field 研究方向
 * @property phoneNumber 手机号
 * @property qQ QQ
 * @property weChat 微信
 * @property email 邮箱
 * @property introduction 个人简介
 * @property taught 近四个学期主讲课程
 * @property teaching 下学期计划开设课程
 * @property philosophy 教学理念
 * @property slogan 最想对学生说的话
 */
data class Teacher(
    val name: String,
    val gender: String,
    val politics: String,
    val nation: String,
    val duty: String,
    val title: String,
    val category: String,
    val department: String,
    val office: String,
    val qualifications: String,
    val degree: String,
    val field: String,
    val phoneNumber: String,
    val qQ: String,
    val weChat: String,
    val email: String,
    val introduction: String,
    val taught: List<CourseItem>,
    val teaching: List<CourseItem>,
    val philosophy: String,
    val slogan: String
)


/**
 * 授课项
 * @property name 课程名
 * @property sort 分类
 * @property semester 学期
 */
data class CourseItem(
    val name: String,
    val sort: String,
    val semester: String
)